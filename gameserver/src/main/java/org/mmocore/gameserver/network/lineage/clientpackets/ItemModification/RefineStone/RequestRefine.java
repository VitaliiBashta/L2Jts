package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.RefineStone;

import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractRefinePacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExVariationResult;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.VariationUtils;

public final class RequestRefine extends AbstractRefinePacket {
    // format: (ch)dddd
    private int targetItemObjId, refinerItemObjId, gemstoneItemObjId;
    private long gemstoneCount;

    @Override
    protected void readImpl() {
        targetItemObjId = readD();
        refinerItemObjId = readD();
        gemstoneItemObjId = readD();
        gemstoneCount = readQ(); // FIXME - похоже тут проблема клиента, либо
        // что-то с размерами пакета. Всегда +1 к
        // числу О_о
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || gemstoneCount < 1) {
            Log.audit("[Refine]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            return;
        }
        gemstoneCount--;
        ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(targetItemObjId);
        ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(refinerItemObjId);
        ItemInstance gemstoneItem = activeChar.getInventory().getItemByObjectId(gemstoneItemObjId);
        if (targetItem == null || refinerItem == null || gemstoneItem == null) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            activeChar.sendPacket(new ExVariationResult(0, 0, 0), SystemMsg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }
        LifeStoneInfo lsi = LifeStoneManager.getStoneInfo(refinerItem.getItemId());
        if (lsi == null) {
            Log.audit("[Refine]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            return;
        }
        if (!isValid(activeChar, targetItem, refinerItem, gemstoneItem)) {
            activeChar.sendPacket(new ExVariationResult(0, 0, 0), SystemMsg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }
        if (VariationUtils.tryAugmentItem(activeChar, targetItem, refinerItem, gemstoneItem, gemstoneCount)) {
            activeChar.sendPacket(new ExVariationResult(targetItem.getVariation1Id(), targetItem.getVariation2Id(), 1), SystemMsg.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED);
        } else {
            activeChar.sendPacket(new ExVariationResult(0, 0, 0), SystemMsg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
        }
    }
}