package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.PetDataHolder;
import org.mmocore.gameserver.database.dao.impl.PetDAO;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

/**
 * format:		cdd
 * format:		cdQ - Gracia Final
 */
public class RequestDestroyItem extends L2GameClientPacket {
    private int _objectId;
    private long _count;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _count = readQ();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        long count = _count;

        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (count < 1) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
            return;
        }

        if (!activeChar.isGM() && item.isHeroWeapon()) {
            activeChar.sendPacket(SystemMsg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            return;
        }

        if (activeChar.getServitor() != null && activeChar.getServitor().getControlItemObjId() == item.getObjectId()) {
            activeChar.sendPacket(SystemMsg.AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if (activeChar.isMounted() && activeChar.getMountObjId() == item.getObjectId()) {
            activeChar.sendPacket(SystemMsg.AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if (!activeChar.isGM() && !item.canBeDestroyed(activeChar)) {
            activeChar.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_DISCARDED);
            return;
        }

        if (_count > item.getCount()) {
            count = item.getCount();
        }

        boolean crystallize = item.canBeCrystallized(activeChar);

        final int crystalId = item.getTemplate().getCrystalType().cry;
        final int crystalAmount = item.getTemplate().getCrystalCount();
        if (crystallize) {
            final int level = activeChar.getSkillLevel(Skill.SKILL_CRYSTALLIZE);
            if (level < 1 || crystalId - ItemTemplate.CRYSTAL_D + 1 > level) {
                crystallize = false;
            }
        }

        Log.items(activeChar, Log.Delete, item, count);

        if (!activeChar.getInventory().destroyItemByObjectId(_objectId, count)) {
            activeChar.sendActionFailed();
            return;
        }

        // При удалении ошейника, удалить пета
        if (PetDataHolder.getInstance().isPetControlItem(item.getItemId())) {
            PetDAO.getInstance().delete(item.getObjectId());

            final Servitor summon = activeChar.getServitor();
            if (summon != null && summon.getControlItemObjId() == item.getObjectId()) {
                summon.unSummon(false, false);
            }

            if (activeChar.isMounted() && activeChar.getMountObjId() == item.getObjectId()) {
                activeChar.dismount();
            }
        }

        if (crystallize) {
            activeChar.sendPacket(SystemMsg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);
            ItemFunctions.addItem(activeChar, crystalId, crystalAmount, true);
        } else {
            activeChar.sendPacket(SystemMessage.removeItems(item.getItemId(), count));
        }
        activeChar.sendChanges();
    }
}
