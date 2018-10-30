package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.RefineStone;

import org.jts.dataparser.data.holder.variationdata.support.VariationFee;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.manager.VariationManager;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractRefinePacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExPutIntensiveResultForVariationMake;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class RequestConfirmRefinerItem extends AbstractRefinePacket {
    // format: (ch)dd
    private int targetItemObjId;
    private int refinerItemObjId;

    @Override
    protected void readImpl() {
        targetItemObjId = readD();
        refinerItemObjId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(targetItemObjId);
        ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(refinerItemObjId);
        if (targetItem == null || refinerItem == null) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        LifeStoneInfo lsi = LifeStoneManager.getStoneInfo(refinerItem.getItemId());
        if (lsi == null) {
            return;
        }
        if (!isValid(activeChar, targetItem, refinerItem)) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        final VariationFee fee = VariationManager.getInstance().getFee(targetItem.getTemplate().getVariationGroupId(), refinerItem.getItemId());
        if (!targetItem.getTemplate().isPvp() && !AllSettingsConfig.allowAugmentPvp)
            if (fee == null) {
                activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
                return;
            }
        final int feeItemId = fee != null ? fee.getFeeItemId() : 2132;
        final long feeCount = fee != null ? fee.getFeeItemCount() : AllSettingsConfig.augmentPvpGemCount;
        SystemMessage sm = new SystemMessage(SystemMsg.REQUIRES_S2_S1).addNumber(feeCount).addItemName(feeItemId);
        activeChar.sendPacket(new ExPutIntensiveResultForVariationMake(refinerItemObjId, refinerItem.getItemId(), feeItemId, feeCount), sm);
    }
}