package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.RefineStone;

import org.jts.dataparser.data.holder.variationdata.support.VariationFee;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractRefinePacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExPutCommissionResultForVariationMake;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.VariationUtils;

public class RequestConfirmGemStone extends AbstractRefinePacket {
    // format: (ch)dddd
    private int targetItemObjId;
    private int refinerItemObjId;
    private int gemstoneItemObjId;
    private long gemstoneCount;

    @Override
    protected void readImpl() {
        targetItemObjId = readD();
        refinerItemObjId = readD();
        gemstoneItemObjId = readD();
        gemstoneCount = readQ();
    }

    @Override
    protected void runImpl() {
        if (gemstoneCount <= 0) {
            return;
        }
        Player activeChar = getClient().getActiveChar();
        ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(targetItemObjId);
        ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(refinerItemObjId);
        ItemInstance gemstoneItem = activeChar.getInventory().getItemByObjectId(gemstoneItemObjId);
        if (targetItem == null || refinerItem == null || gemstoneItem == null) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        LifeStoneInfo lsi = LifeStoneManager.getStoneInfo(refinerItem.getItemId());
        if (lsi == null) {
            return;
        }
        if (!isValid(activeChar, targetItem, refinerItem, gemstoneItem)) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        final VariationFee fee = VariationUtils.getVariationFee(targetItem, refinerItem);
        if (fee == null) {
            if (targetItem.getTemplate().isPvp() && AllSettingsConfig.allowAugmentPvp) {
                if (gemstoneItem.getItemId() != 2132) {
                    activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
                    return;
                }
                if (gemstoneCount != AllSettingsConfig.augmentPvpGemCount) {
                    activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                    return;
                }
            } else {
                activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
                return;
            }
        } else {
            if (fee.getFeeItemId() != gemstoneItem.getItemId()) {
                activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
                return;
            }
            if (gemstoneCount != fee.getFeeItemCount()) {
                activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                return;
            }
        }
        activeChar.sendPacket(new ExPutCommissionResultForVariationMake(gemstoneItemObjId, gemstoneCount), SystemMsg.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN);
    }
}