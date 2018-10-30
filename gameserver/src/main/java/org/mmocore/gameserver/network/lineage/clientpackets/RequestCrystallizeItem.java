package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

public class RequestCrystallizeItem extends L2GameClientPacket {
    //Format: cdd

    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
        long unk = readQ();
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

        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (item.isHeroWeapon()) {
            activeChar.sendPacket(SystemMsg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            return;
        }

        if (!item.canBeCrystallized(activeChar)) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        //can player crystallize?
        final int level = activeChar.getSkillLevel(Skill.SKILL_CRYSTALLIZE);
        if (item.getTemplate().getItemGrade().externalOrdinal > level) {
            activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM);
            activeChar.sendActionFailed();
            return;
        }

        final int crystalAmount = item.getTemplate().getCrystalCount(item.getEnchantLevel(), false);
        final int crystalId = item.getTemplate().getCrystalType().cry;

        Log.items(activeChar, Log.Crystalize, item);

        if (!activeChar.getInventory().destroyItemByObjectId(_objectId, 1L)) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_CRYSTALLIZED).addItemName(item.getItemId()));
        ItemFunctions.addItem(activeChar, crystalId, crystalAmount, true);
        activeChar.sendChanges();
    }
}