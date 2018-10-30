package org.mmocore.gameserver.network.lineage.clientpackets.GoodsInventory;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.GoodsInventory.ExGoodsInventoryResult;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.PremiumItem;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class RequestUseGoodsInventoryItem extends L2GameClientPacket {
    private int goodsType;
    private long itemId;
    private long itemCount;

    @Override
    protected void readImpl() throws Exception {
        goodsType = readC();
        itemId = readQ();
        if (goodsType != 0) {
            itemCount = readQ();
        }
    }

    @Override
    protected void runImpl() throws Exception {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(ExGoodsInventoryResult.PREVIOS_REQUEST_IS_NOT_COMPLETE);
        }
        if (activeChar.isActionsDisabled()) {
            activeChar.sendPacket(ExGoodsInventoryResult.TRY_AGAIN_LATER);
        }
        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(ExGoodsInventoryResult.CANT_USE_AT_TRADE_OR_PRIVATE_SHOP);
        }
        if (!activeChar.isInventoryLimit(false) || !activeChar.isWeightLimit(false)) {
            activeChar.sendPacket(ExGoodsInventoryResult.INVENTORY_FULL);
        }
        if (AuthServerCommunication.getInstance().isShutdown()) {
            activeChar.sendPacket(ExGoodsInventoryResult.NOT_CONNECT_TO_PRODUCT_SERVER);
        }
        if (goodsType == 1) {
            // TODO[K] - систему с получением списка предметов на аутхСервере
        } else {
            if (activeChar.getPremiumAccountComponent().getPremiumItemList().isEmpty()) {
                activeChar.sendPacket(ExGoodsInventoryResult.NOT_EXISTS);
            }

            final PremiumItem premiumItem = activeChar.getPremiumAccountComponent().getPremiumItemList().get(itemId);
            if (premiumItem == null) {
                activeChar.sendPacket(ExGoodsInventoryResult.NOT_EXISTS);
            }

            final boolean stackable = ItemTemplateHolder.getInstance().getTemplate(premiumItem.getItemId()).isStackable();
            if (premiumItem.getCount() < itemCount) {
                return;
            }
            if (!stackable) {
                for (int i = 0; i < itemCount; i++) {
                    ItemFunctions.addItem(activeChar, premiumItem.getItemId(), 1);
                }
            } else {
                ItemFunctions.addItem(activeChar, premiumItem.getItemId(), itemCount);
            }

            if (itemCount < premiumItem.getCount()) {
                activeChar.getPremiumAccountComponent().getPremiumItemList().get(itemId).updateCount(premiumItem.getCount() - itemCount);
                AccountBonusDAO.getInstance().updatePremiumItem((int) itemId, premiumItem.getCount() - itemCount, activeChar.getObjectId());
            } else {
                activeChar.getPremiumAccountComponent().getPremiumItemList().remove(itemId);
                AccountBonusDAO.getInstance().deletePremiumItem((int) itemId, activeChar.getObjectId());
            }
            activeChar.sendPacket(ExGoodsInventoryResult.SUCCESS);
        }
    }
}