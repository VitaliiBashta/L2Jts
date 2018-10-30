package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SendTradeDone;
import org.mmocore.gameserver.network.lineage.serverpackets.TradeOtherAdd;
import org.mmocore.gameserver.network.lineage.serverpackets.TradeOwnAdd;
import org.mmocore.gameserver.network.lineage.serverpackets.TradeUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;

import java.util.List;


public class AddTradeItem extends L2GameClientPacket {
    private int _objectId;
    private long _amount;

    @Override
    protected void readImpl() {
        int _tradeId = readD();
        _objectId = readD();
        _amount = readQ();
    }

    @Override
    protected void runImpl() {
        final Player parthner1 = getClient().getActiveChar();
        if (parthner1 == null || _amount < 1) {
            return;
        }

        final Request request = parthner1.getRequest();
        if (request == null || !request.isTypeOf(L2RequestType.TRADE)) {
            parthner1.sendActionFailed();
            return;
        }

        if (!request.isInProgress()) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendActionFailed();
            return;
        }

        if (parthner1.isOutOfControl()) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendActionFailed();
            return;
        }

        final Player parthner2 = request.getOtherPlayer(parthner1);
        if (parthner2 == null) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            parthner1.sendActionFailed();
            return;
        }

        if (parthner2.getRequest() != request) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendActionFailed();
            return;
        }

        if (request.isConfirmed(parthner1) || request.isConfirmed(parthner2)) {
            parthner1.sendPacket(SystemMsg.YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED);
            parthner1.sendActionFailed();
            return;
        }

        final ItemInstance item = parthner1.getInventory().getItemByObjectId(_objectId);
        if (item == null || !item.canBeTraded(parthner1)) {
            parthner1.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD);
            return;
        }

        long count = Math.min(_amount, item.getCount());

        final List<TradeItem> tradeList = parthner1.getTradeList();
        TradeItem tradeItem = null;

        try {
            for (final TradeItem ti : parthner1.getTradeList()) {
                if (ti.getObjectId() == _objectId) {
                    count = Math.addExact(count, ti.getCount());
                    count = Math.min(count, item.getCount());
                    ti.setCount(count);
                    tradeItem = ti;
                    break;
                }
            }
        } catch (ArithmeticException ae) {
            parthner1.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
            return;
        }

        if (tradeItem == null) {
            // добавляем новую вещь в список
            tradeItem = new TradeItem(item);
            tradeItem.setCount(count);
            tradeList.add(tradeItem);
        }

        parthner1.sendPacket(new TradeOwnAdd(tradeItem, tradeItem.getCount()), new TradeUpdate(tradeItem, item.getCount() - tradeItem.getCount()));
        parthner2.sendPacket(new TradeOtherAdd(tradeItem, tradeItem.getCount()));
    }
}