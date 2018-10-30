package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TradeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Список продаваемого в приватный магазин покупки
 */
public class RequestPrivateStoreBuySellList extends L2GameClientPacket {
    private int _buyerId, _count;
    private TradeItem[] _items; // object id
    private long[] _itemQ; // count
    private long[] _itemP; // price

    @Override
    protected void readImpl() {
        _buyerId = readD();
        _count = readD();

        if (_count * 28 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[PrivateStoreBuySellList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count, m.b used PH!");
            _count = 0;
            return;
        }

        _items = new TradeItem[_count];

        for (int i = 0; i < _count; i++) {
            TradeItem item = new TradeItem();
            item.setObjectId(readD());
            item.setItemId(readD());
            item.setEnchantLevel(readH());
            readH();
            item.setCount(readQ());
            item.setOwnersPrice(readQ());

            if (item.getCount() < 1 || item.getOwnersPrice() < 1 || ArrayUtils.indexOf(_items, _items[i]) < i) {
                _count = 0;
                break;
            }
            _items[i] = item;
        }
    }

    @Override
    protected void runImpl() {
        final Player seller = getClient().getActiveChar();
        if (seller == null || _count == 0) {
            return;
        }

        if (seller.isActionsDisabled()) {
            seller.sendActionFailed();
            return;
        }

        if (seller.isInStoreMode()) {
            seller.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (seller.isInTrade()) {
            seller.sendActionFailed();
            return;
        }

        if (seller.isFishing()) {
            seller.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (!seller.getPlayerAccess().UseTrade) {
            seller.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
            return;
        }

        final Player buyer = (Player) seller.getVisibleObject(_buyerId);
        if (buyer == null || buyer.getPrivateStoreType() != Player.STORE_PRIVATE_BUY || !seller.isInRangeZ(buyer, seller.getInteractDistance(buyer))) {
            seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
            seller.sendActionFailed();
            return;
        }

        final List<TradeItem> buyList = buyer.getBuyList();
        if (buyList.isEmpty()) {
            seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
            seller.sendActionFailed();
            return;
        }

        final List<TradeItem> sellList = new ArrayList<>();

        long totalCost = 0;
        int slots = 0;
        long weight = 0;

        buyer.getInventory().writeLock();
        seller.getInventory().writeLock();
        try {
            loop:
            for (int i = 0; i < _count; i++) {
                TradeItem tradeItem = _items[i];
                final ItemInstance item = seller.getInventory().getItemByObjectId(tradeItem.getObjectId());
                if (item == null || item.getCount() < tradeItem.getCount() || !item.canBeTraded(seller)) {
                    break loop;
                }

                TradeItem si = null;

                for (final TradeItem bi : buyList) {
                    if (bi.getItemId() == item.getItemId()) {
                        if (bi.getOwnersPrice() == tradeItem.getOwnersPrice()) {
                            if (tradeItem.getCount() > bi.getCount()) {
                                break loop;
                            }

                            totalCost = Math.addExact(totalCost, Math.multiplyExact(tradeItem.getCount(), tradeItem.getOwnersPrice()));
                            weight = Math.addExact(weight, Math.multiplyExact(tradeItem.getCount(), item.getTemplate().getWeight()));
                            if (!item.isStackable() || buyer.getInventory().getItemByItemId(item.getItemId()) == null) {
                                slots++;
                            }

                            si = new TradeItem();
                            si.setObjectId(tradeItem.getObjectId());
                            si.setItemId(item.getItemId());
                            si.setCount(tradeItem.getCount());
                            si.setOwnersPrice(tradeItem.getOwnersPrice());

                            sellList.add(si);
                            break;
                        }
                    }
                }
            }
        } catch (ArithmeticException ae) {
            Log.audit("[PrivateStoreBuySellList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
            sellList.clear();
            seller.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
            return;
        } finally {
            try {
                if (sellList.size() != _count) {
                    seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                }

                if (!buyer.getInventory().validateWeight(weight)) {
                    buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                }

                if (!buyer.getInventory().validateCapacity(slots)) {
                    buyer.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                    seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                }

                if (!buyer.reduceAdena(totalCost)) {
                    buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    seller.sendPacket(SystemMsg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    seller.sendActionFailed();
                    return;
                }

                ItemInstance item;
                for (final TradeItem si : sellList) {
                    item = seller.getInventory().removeItemByObjectId(si.getObjectId(), si.getCount());
                    for (final TradeItem bi : buyList) {
                        if (bi.getItemId() == si.getItemId()) {
                            if (bi.getOwnersPrice() == si.getOwnersPrice()) {
                                bi.setCount(bi.getCount() - si.getCount());
                                if (bi.getCount() < 1L) {
                                    buyList.remove(bi);
                                }
                                break;
                            }
                        }
                    }
                    Log.items(seller, Log.PrivateStoreSell, item);
                    Log.items(buyer, Log.PrivateStoreBuy, item);
                    buyer.getInventory().addItem(item);
                    TradeHelper.purchaseItem(buyer, seller, si);
                }

                final long tax = TradeHelper.getTax(seller, totalCost);
                if (tax > 0) {
                    totalCost -= tax;
                    seller.sendMessage(new CustomMessage("trade.HavePaidTax").addNumber(tax));
                }

                seller.addAdena(totalCost);
                buyer.saveTradeList();
                final ItemInstance adenaSeller = seller.getInventory().getItemByItemId(ItemTemplate.ITEM_ID_ADENA);
                final ItemInstance adenaBuyer = buyer.getInventory().getItemByItemId(ItemTemplate.ITEM_ID_ADENA);
                Log.items(seller, Log.PrivateStoreSellerGiveAdena, adenaSeller, totalCost);
                Log.items(buyer, Log.PrivateStoreBuyerDeleteAdena, adenaBuyer, totalCost);
            } finally {
                seller.getInventory().writeUnlock();
                buyer.getInventory().writeUnlock();
            }
        }

        if (buyList.isEmpty()) {
            TradeHelper.cancelStore(buyer);
        }

        seller.sendChanges();
        buyer.sendChanges();

        seller.sendActionFailed();
    }
}