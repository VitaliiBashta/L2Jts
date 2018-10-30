package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PrivateStoreManageListBuy;
import org.mmocore.gameserver.network.lineage.serverpackets.PrivateStoreMsgBuy;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TradeHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SetPrivateStoreBuyList extends L2GameClientPacket {
    private TradeItem[] _items;
    private int _count;

    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 40 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[SendWareHouseWithDrawList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count, m.b used PH!");
            _count = 0;
            return;
        }
        _items = new TradeItem[_count];
        for (int i = 0; i < _count; i++) {
            final TradeItem item = new TradeItem();
            item.setItemId(readD());
            item.setEnchantLevel(readH());
            readH();
            item.setCount(readQ());
            item.setOwnersPrice(readQ());

            if (item.getCount() < 1 || item.getOwnersPrice() < 1) {
                _count = 0;
                break;
            }

            // TODO Gracia Final
            readC(); // FE
            readD(); // FF 00 00 00
            readD(); // 00 00 00 00

            readC(); // Unknown 7 bytes
            readC();
            readC();
            readC();
            readC();
            readC();
            readC();
            _items[i] = item;
        }
    }

    @Override
    protected void runImpl() {
        final Player buyer = getClient().getActiveChar();
        if (buyer == null || _count == 0) {
            return;
        }

        if (!TradeHelper.checksIfCanOpenStore(buyer, Player.STORE_PRIVATE_BUY)) {
            buyer.sendActionFailed();
            return;
        }

        final List<TradeItem> buyList = new CopyOnWriteArrayList<>();
        long totalCost = 0;
        int slots = 0;
        long weight = 0;
        try {
            loop:
            for (int i = 0; i < _count; i++) {
                TradeItem tradeItem = _items[i];
                final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(tradeItem.getItemId());

                if (item == null || tradeItem.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                    continue;
                }

                if (item.isStackable()) {
                    for (final TradeItem bi : buyList) {
                        if (bi.getItemId() == tradeItem.getItemId()) {
                            bi.setOwnersPrice(tradeItem.getOwnersPrice());
                            bi.setCount(bi.getCount() + tradeItem.getCount());
                            totalCost = Math.addExact(totalCost, Math.multiplyExact(tradeItem.getCount(), tradeItem.getOwnersPrice()));
                            continue loop;
                        }
                    }
                }

                final TradeItem bi = new TradeItem();
                bi.setItemId(tradeItem.getItemId());
                bi.setCount(tradeItem.getCount());
                bi.setOwnersPrice(tradeItem.getOwnersPrice());
                totalCost = Math.addExact(totalCost, Math.multiplyExact(tradeItem.getCount(), tradeItem.getOwnersPrice()));
                weight = Math.addExact(weight, Math.multiplyExact(tradeItem.getCount(), item.getWeight()));
                if (!item.isStackable() || buyer.getInventory().getItemByItemId(item.getItemId()) == null) {
                    slots++;
                }
                buyList.add(bi);
            }
        } catch (ArithmeticException ae) {
            Log.audit("[SetPrivateStoreBuyList]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
            buyer.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
            return;
        }

        if (buyList.size() > buyer.getTradeLimit()) {
            buyer.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
            buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
            return;
        }

        if (totalCost > buyer.getAdena()) {
            buyer.sendPacket(SystemMsg.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
            buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
            return;
        }

        if (!buyer.getInventory().validateWeight(weight) || !buyer.getInventory().validateCapacity(slots)) {
            buyer.sendPacket(SystemMsg.THE_WEIGHT_AND_VOLUME_LIMIT_OF_YOUR_INVENTORY_CANNOT_BE_EXCEEDED);
            buyer.sendPacket(new PrivateStoreManageListBuy(buyer));
            return;
        }

        if (!buyList.isEmpty()) {
            buyer.setBuyList(buyList);
            buyer.saveTradeList();
            buyer.setPrivateStoreType(Player.STORE_PRIVATE_BUY);
            buyer.broadcastPacket(new PrivateStoreMsgBuy(buyer));
            buyer.sitDown(null);
            buyer.broadcastCharInfo();
        }

        buyer.sendActionFailed();
    }
}