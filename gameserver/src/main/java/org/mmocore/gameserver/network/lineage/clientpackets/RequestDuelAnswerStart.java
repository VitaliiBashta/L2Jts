package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.AbstractDuelEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

public class RequestDuelAnswerStart extends L2GameClientPacket {
    private int _response;
    private int _duelType;

    @Override
    protected void readImpl() {
        _duelType = readD();
        readD(); // 1 посылается если ниже  -1(при включеной опции клиента Отменять дуели)
        _response = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Request request = activeChar.getRequest();
        if (request == null || !request.isTypeOf(L2RequestType.DUEL)) {
            return;
        }

        if (!request.isInProgress()) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isActionsDisabled()) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        final Player requestor = request.getRequester();
        if (requestor == null) {
            request.cancel();
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            activeChar.sendActionFailed();
            return;
        }

        if (requestor.getRequest() != request) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        if (_duelType != request.getInteger("duelType")) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        final AbstractDuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, _duelType);

        switch (_response) {
            case 0:
                request.cancel();
                if (_duelType == 1) {
                    requestor.sendPacket(SystemMsg.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
                } else {
                    requestor.sendPacket(new SystemMessage(SystemMsg.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL).addName(activeChar));
                }
                break;
            case -1:
                request.cancel();
                requestor.sendPacket(new SystemMessage(SystemMsg.C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST).addName(
                        activeChar));
                break;
            case 1:
                if (!duelEvent.canDuel(requestor, activeChar, false)) {
                    request.cancel();
                    return;
                }

                final SystemMessage msg1;
                final SystemMessage msg2;
                if (_duelType == 1) {
                    msg1 = new SystemMessage(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_TO_A_PARTY_DUEL);
                    msg2 = new SystemMessage(SystemMsg.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY);
                } else {
                    msg1 = new SystemMessage(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_A_DUEL);
                    msg2 = new SystemMessage(SystemMsg.C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL);
                }

                activeChar.sendPacket(msg1.addName(requestor));
                requestor.sendPacket(msg2.addName(activeChar));

                try {
                    duelEvent.createDuel(requestor, activeChar, request.getInteger("arenaId", 0));
                } finally {
                    request.done();
                }
                break;
        }
    }
}