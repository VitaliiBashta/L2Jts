package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.CustomBuyListHolder;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.buylist.Product;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuySellList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemHolder;
import org.mmocore.gameserver.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * format:		cddb, b - array of (dd)
 */
public class RequestBuyItem extends L2GameClientPacket {
    private int _listId;
    private int _count;
    private List<ItemHolder> items;

    @Override
    protected void readImpl() {
        _listId = readD();
        _count = readD();
        if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[BuyItem]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count!");
            _count = 0;
            return;
        }

        items = new ArrayList<>(_count);
        for (int i = 0; i < _count; i++) {
            int itemId = readD();
            long count = readQ();
            if ((itemId < 1) || (count < 1)) {
                items = null;
                return;
            }
            items.add(new ItemHolder(itemId, count));
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0) {
            return;
        }

        if (items == null) {
            activeChar.sendActionFailed();
            return;
        }

        // Проверяем, не подменили ли id
        if (activeChar.getBuyListId() != _listId) {
            Log.audit("[BuyItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " changed buy list Id. Used packetHach or another programm!");
            return;
        }

        if (activeChar.isCantBuy()) {
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

        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        final NpcInstance merchant = activeChar.getLastNpc();
        final boolean isValidMerchant = merchant != null && merchant.isMerchantNpc() || merchant != null && merchant.isTeleportNpc();
        if (!activeChar.isGM() && (merchant == null || !isValidMerchant || !activeChar.isInRangeZ(merchant, activeChar.getInteractDistance(merchant)))) {
            Log.audit("[BuyItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " isInRange > 200 || null");
            activeChar.sendActionFailed();
            return;
        }

        final BuyList buyList;
        if (merchant != null) {
            buyList = merchant.getTemplate().getTradeList(_listId);
        } else if (activeChar.isGM()) {
            buyList = CustomBuyListHolder.getInstance().getBuyList(_listId);
        } else {
            buyList = null;
        }
        if (buyList == null) {
            Log.audit("[BuyList]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " buy list not found. Used PH!");
            activeChar.sendActionFailed();
            return;
        }
        int slots = 0;
        long weight = 0;
        long totalPrice = 0;
        long tax = 0;
        double taxRate = 0;

        Castle castle = null;
        if (merchant != null) {
            castle = merchant.getCastle(activeChar);
            if (castle != null) {
                taxRate = castle.getTaxRate();
            }
        }
        try {
            for (final ItemHolder i : items) {
                final int itemId = i.getItemId();
                final Product product = buyList.getProductByItemId(i.getItemId());
                if (product == null) {
                    Log.audit("[BuyItem]", "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false BuyList listId " + _listId + " and item_id " + i.getItemId());
                    activeChar.sendActionFailed();
                    return;
                }
                if (product.hasLimitedStock() && product.getMaxCount() < i.getCount()) {
                    continue;
                }
                if (!product.getItem().isStackable() && (i.getCount() > 1)) {
                    Log.audit("[BuyItem]", "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to purchase invalid quantity of items at the same time.");
                    sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED));
                    return;
                }
                final long price = product.getPrice();
                if (price < 0) {
                    Log.audit("[BuyItem]", "\"ERROR, no price found .. wrong buylist ??");
                    activeChar.sendActionFailed();
                    return;
                }
                if (price == 0 && (!activeChar.isGM() || !activeChar.getPlayerAccess().UseGMShop)) {
                    Log.audit("[BuyItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " trying buy item_id " + itemId + " from price 0. Used PH!");
                    activeChar.sendActionFailed();
                    return;
                }
                if (product.hasLimitedStock()) {
                    if (i.getCount() > product.getCount()) {
                        activeChar.sendActionFailed();
                        return;
                    }
                }
                totalPrice = Math.addExact(totalPrice, Math.multiplyExact(i.getCount(), price));
                weight = Math.addExact(weight, Math.multiplyExact(i.getCount(), product.getItem().getWeight()));
                if (activeChar.getInventory().getItemByItemId(product.getItemId()) == null) {
                    slots++;
                }
            }
            tax = (long) (totalPrice * taxRate);
            totalPrice = Math.addExact(totalPrice, tax);
            if (!activeChar.getInventory().validateWeight(weight)) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!activeChar.getInventory().validateCapacity(slots)) {
                activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }

            if (!activeChar.reduceAdena(totalPrice)) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            // Proceed the purchase
            for (final ItemHolder i : items) {
                final Product product = buyList.getProductByItemId(i.getItemId());
                if (product == null) {
                    Log.audit("[BuyItem]", "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false BuyList listId " + _listId + " and item_id " + i.getItemId());
                    continue;
                }

                if (product.hasLimitedStock()) {
                    if (product.decreaseCount(i.getCount())) {
                        activeChar.getInventory().addItem(i.getItemId(), i.getCount());
                    }
                } else {
                    activeChar.getInventory().addItem(i.getItemId(), i.getCount());
                }
            }

            // Add tax to castle treasury if not owned by npc clan
            if (castle != null) {
                if (tax > 0 && castle.getOwnerId() > 0 && activeChar.getReflection() == ReflectionManager.DEFAULT) {
                    castle.addToTreasury(tax, true, false);
                }
            }
        } catch (ArithmeticException ae) {
            Log.audit("[BuyItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " Exception int main method, m.b used PH!");
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        sendPacket(new ExBuySellList(activeChar, true));
        activeChar.sendChanges();
    }
}
