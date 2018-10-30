package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

public class RequestPetUseItem extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
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

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        activeChar.setActive();

        if (activeChar.getServitor() == null || !activeChar.getServitor().isPet()) {
            activeChar.sendActionFailed();
            return;
        }

        final PetInstance pet = (PetInstance) activeChar.getServitor();
        if (pet == null) {
            return;
        }

        final ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);

        if (item == null || item.getCount() < 1) {
            return;
        }

        if (activeChar.isAlikeDead() || pet.isDead() || pet.isOutOfControl() || !item.getTemplate().testCondition(pet, item)) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return;
        }

        // manual pet feeding
        if (ArrayUtils.contains(pet.getFoodId(), item.getItemId()) || ArrayUtils.contains(AllSettingsConfig.ALT_ALLOWED_PET_POTIONS, item.getItemId())) {
            item.getTemplate().getHandler().useItem(pet, item, false);
            return;
        }

        final IBroadcastPacket sm = ItemFunctions.checkIfCanEquip(pet, item);
        if (sm == null) {
            if (item.isEquipped()) {
                pet.getInventory().unEquipItem(item);
            } else {
                pet.getInventory().equipItem(item);
            }
            pet.broadcastCharInfo();
            return;
        }

        activeChar.sendPacket(sm);
    }
}