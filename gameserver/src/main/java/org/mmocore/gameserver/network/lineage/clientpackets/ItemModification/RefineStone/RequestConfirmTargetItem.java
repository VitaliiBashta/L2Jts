package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.RefineStone;

import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractRefinePacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExPutItemResultForVariationMake;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

public class RequestConfirmTargetItem extends AbstractRefinePacket {
    // format: (ch)d
    private int _itemObjId;

    @Override
    protected void readImpl() {
        _itemObjId = readD(); // object_id шмотки
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);
        if (item == null) {
            Log.audit("[RefineTargetItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            activeChar.sendActionFailed();
            return;
        }
        if (!isValid(activeChar, item)) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        if (!isValid(item, activeChar))
            return;
        activeChar.sendPacket(new ExPutItemResultForVariationMake(_itemObjId), SystemMsg.SELECT_THE_CATALYST_FOR_AUGMENTATION);
    }
}