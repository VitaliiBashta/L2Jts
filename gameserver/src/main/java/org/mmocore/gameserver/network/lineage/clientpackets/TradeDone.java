package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SendTradeDone;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.TradePressOtherOk;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.utils.Log;

import java.util.List;

/**
 * Вызывается при нажатии кнопки OK в окне обмена.
 */
public class TradeDone extends L2GameClientPacket {
    private int _response;

    @Override
    protected void readImpl() {
        _response = readD();
    }

    @Override
    protected void runImpl() {
        final Player parthner1 = getClient().getActiveChar();
        if (parthner1 == null) {
            return;
        }

        final Request request = parthner1.getRequest();
        if (request == null || !request.isTypeOf(L2RequestType.TRADE)) {
            Log.audit("[TradeDone]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " request player not found, m.b used PH");
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

        if (parthner1.isInStoreMode()) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            parthner1.sendActionFailed();
            return;
        }

        final Player parthner2 = request.getOtherPlayer(parthner1);
        if (parthner2 == null) {
            Log.audit("[TradeDone]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " requester player not found, m.b used PH");
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            parthner1.sendActionFailed();
            return;
        }

        if (parthner2.isInStoreMode()) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            parthner1.sendActionFailed();
            return;
        }

        if (parthner2.getRequest() != request) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner1.sendActionFailed();
            return;
        }

        if (_response == 0) {
            request.cancel();
            parthner1.sendPacket(SendTradeDone.FAIL);
            parthner2.sendPacket(SendTradeDone.FAIL, new SystemMessage(SystemMsg.C1_HAS_CANCELLED_THE_TRADE).addString(parthner1.getName()));
            return;
        }

        if (!parthner1.isInRangeZ(parthner2, parthner1.getInteractDistance(parthner2))) {
            request.cancel();
            final SystemMessage msg = new SystemMessage(SystemMsg.C1_HAS_CANCELLED_THE_TRADE).addString(parthner1.getName());
            parthner1.sendPacket(SendTradeDone.FAIL, msg);
            parthner2.sendPacket(SendTradeDone.FAIL, msg);
            parthner1.sendActionFailed();
            parthner2.sendActionFailed();
            return;
        }

        // first party accepted the trade
        // notify clients that "OK" button has been pressed.
        request.confirm(parthner1);
        parthner2.sendPacket(new SystemMessage(SystemMsg.C1_HAS_CONFIRMED_THE_TRADE).addString(parthner1.getName()), TradePressOtherOk.STATIC);

        if (!request.isConfirmed(parthner2)) // Check for dual confirmation
        {
            parthner1.sendActionFailed();
            return;
        }

        final List<TradeItem> tradeList1 = parthner1.getTradeList();
        final List<TradeItem> tradeList2 = parthner2.getTradeList();
        int slots = 0;
        long weight = 0;
        boolean success = false;

        parthner1.getInventory().writeLock();
        parthner2.getInventory().writeLock();
        try {
            slots = 0;
            weight = 0;

            for (final TradeItem ti : tradeList1) {
                final ItemInstance item = parthner1.getInventory().getItemByObjectId(ti.getObjectId());
                if (item == null || item.getCount() < ti.getCount() || !item.canBeTraded(parthner1)) {
                    return;
                }

                weight = Math.addExact(weight, Math.multiplyExact(ti.getCount(), ti.getItem().getWeight()));
                if (!ti.getItem().isStackable() || parthner2.getInventory().getItemByItemId(ti.getItemId()) == null) {
                    slots++;
                }
            }

            if (!parthner2.getInventory().validateWeight(weight)) {
                parthner2.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }

            if (!parthner2.getInventory().validateCapacity(slots)) {
                parthner2.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }

            slots = 0;
            weight = 0;

            for (final TradeItem ti : tradeList2) {
                final ItemInstance item = parthner2.getInventory().getItemByObjectId(ti.getObjectId());
                if (item == null || item.getCount() < ti.getCount() || !item.canBeTraded(parthner2)) {
                    return;
                }

                weight = Math.addExact(weight, Math.multiplyExact(ti.getCount(), ti.getItem().getWeight()));
                if (!ti.getItem().isStackable() || parthner1.getInventory().getItemByItemId(ti.getItemId()) == null) {
                    slots++;
                }
            }

            if (!parthner1.getInventory().validateWeight(weight)) {
                parthner1.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }

            if (!parthner1.getInventory().validateCapacity(slots)) {
                parthner1.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }

            for (final TradeItem ti : tradeList1) {
                final ItemInstance item = parthner1.getInventory().removeItemByObjectId(ti.getObjectId(), ti.getCount());

                Log.items(parthner1, Log.TradeSell, item);
                Log.items(parthner2, Log.TradeBuy, item);
                parthner2.getInventory().addItem(item);
            }

            for (final TradeItem ti : tradeList2) {
                final ItemInstance item = parthner2.getInventory().removeItemByObjectId(ti.getObjectId(), ti.getCount());

                Log.items(parthner2, Log.TradeSell, item);
                Log.items(parthner1, Log.TradeBuy, item);
                parthner1.getInventory().addItem(item);
            }

            parthner1.sendPacket(SystemMsg.YOUR_TRADE_WAS_SUCCESSFUL);
            parthner2.sendPacket(SystemMsg.YOUR_TRADE_WAS_SUCCESSFUL);

            success = true;
        } catch (ArithmeticException ae) {
            Log.audit("[TradeDone]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " exception in main method, m.b used PH!");
        } finally {
            parthner2.getInventory().writeUnlock();
            parthner1.getInventory().writeUnlock();

            request.done();

            parthner1.sendPacket(success ? SendTradeDone.SUCCESS : SendTradeDone.FAIL);
            parthner2.sendPacket(success ? SendTradeDone.SUCCESS : SendTradeDone.FAIL);
        }
    }
}
