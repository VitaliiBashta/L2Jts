package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

/**
 * format: (ch)d
 */
public class AnswerJoinPartyRoom extends L2GameClientPacket {
    private int _response;

    @Override
    protected void readImpl() {
        if (_buf.hasRemaining()) {
            _response = readD();
        } else {
            _response = 0;
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Request request = activeChar.getRequest();
        if (request == null || !request.isTypeOf(L2RequestType.PARTY_ROOM)) {
            return;
        }

        if (!request.isInProgress()) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isOutOfControl()) {
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

        // отказ
        if (_response == 0) {
            request.cancel();
            requestor.sendPacket(SystemMsg.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY);
            return;
        }

        if (activeChar.getMatchingRoom() != null) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        try {
            final MatchingRoom room = requestor.getMatchingRoom();
            if (room == null || room.getType() != MatchingRoom.PARTY_MATCHING) {
                return;
            }

            room.addMember(activeChar);
        } finally {
            request.done();
        }
    }
}