package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.RefineStone;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractRefinePacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem.ExVariationCancelResult;
import org.mmocore.gameserver.network.lineage.serverpackets.ShortCutRegister;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.mmocore.gameserver.utils.VariationUtils;

public final class RequestRefineCancel extends AbstractRefinePacket {
    // format: (ch)d
    private int targetItemObjId;

    @Override
    protected void readImpl() {
        targetItemObjId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isActionsDisabled()) {
            activeChar.sendPacket(new ExVariationCancelResult(0));
            return;
        }
        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(new ExVariationCancelResult(0));
            return;
        }
        if (activeChar.isInTrade()) {
            activeChar.sendPacket(new ExVariationCancelResult(0));
            return;
        }
        final ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(targetItemObjId);
        // cannot remove augmentation from a not augmented item
        if (targetItem == null || !targetItem.isAugmented()) {
            activeChar.sendPacket(new ExVariationCancelResult(0), SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            return;
        }
        // get the price
        final long price = VariationUtils.getRemovePrice(targetItem);
        if (price < 0) {
            activeChar.sendPacket(new ExVariationCancelResult(0));
            return;
        }
        // try to reduce the players adena
        if (!activeChar.reduceAdena(price, true)) {
            activeChar.sendPacket(new ExVariationCancelResult(0), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }
        boolean equipped = false;
        if (equipped = targetItem.isEquipped()) {
            activeChar.getInventory().unEquipItem(targetItem);
        }
        // remove the augmentation
        targetItem.setVariationStoneId(0);
        targetItem.setVariation1Id(0);
        targetItem.setVariation2Id(0);
        targetItem.setJdbcState(JdbcEntityState.UPDATED);
        targetItem.update();
        if (equipped) {
            activeChar.getInventory().equipItem(targetItem);
        }
        // send inventory update
        final InventoryUpdate iu = new InventoryUpdate().addModifiedItem(targetItem);
        // send system message
        final SystemMessage sm = new SystemMessage(SystemMsg.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
        sm.addItemName(targetItem.getItemId());
        activeChar.sendPacket(new ExVariationCancelResult(1), iu, sm);
        for (final ShortCut sc : activeChar.getShortCutComponent().getAllShortCuts()) {
            if (sc.getId() == targetItem.getObjectId() && sc.getType() == ShortCut.TYPE_ITEM) {
                activeChar.sendPacket(new ShortCutRegister(activeChar, sc));
            }
        }
        activeChar.sendChanges();
    }
}