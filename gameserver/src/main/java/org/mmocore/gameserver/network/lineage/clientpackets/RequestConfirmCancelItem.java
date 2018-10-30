package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExPutItemResultForVariationCancel;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class RequestConfirmCancelItem extends L2GameClientPacket {
    // format: (ch)d
    int _itemId;

    @Override
    protected void readImpl() {
        _itemId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemId);

        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (!item.isAugmented()) {
            activeChar.sendPacket(SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            return;
        }

        activeChar.sendPacket(new ExPutItemResultForVariationCancel(item));
    }
}