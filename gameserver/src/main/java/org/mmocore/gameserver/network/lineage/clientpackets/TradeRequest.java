package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SendTradeRequest;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Util;

public class TradeRequest extends L2GameClientPacket {
    //Format: cd
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!activeChar.getPlayerAccess().UseTrade) {
            activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_);
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendPacket(SystemMsg.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
            return;
        }

        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        String tradeBan = activeChar.getPlayerVariables().get(PlayerVariables.TRADE_BAN);
        if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
            if (tradeBan.equals("-1")) {
                activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently"));
            } else {
                activeChar.sendMessage(new CustomMessage("common.TradeBanned").addString(Util.formatTime((int) (Long.parseLong(tradeBan) / 1000L - System.currentTimeMillis() / 1000L))));
            }
            return;
        }

        final GameObject target = activeChar.getVisibleObject(_objectId);
        if (target == null || !target.isPlayer() || target == activeChar) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        final Player reciever = (Player) target;
        if (!reciever.getPlayerAccess().UseTrade) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        tradeBan = reciever.getPlayerVariables().get(PlayerVariables.TRADE_BAN);
        if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (reciever.isInBlockList(activeChar)) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT);
            return;
        }

        if (reciever.getTradeRefusal() || reciever.isBusy()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addString(reciever.getName()));
            return;
        }

        new Request(L2RequestType.TRADE_REQUEST, activeChar, reciever).setTimeout(10000L);
        reciever.sendPacket(new SendTradeRequest(activeChar.getObjectId()));
        activeChar.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1).addString(reciever.getName()));
    }
}