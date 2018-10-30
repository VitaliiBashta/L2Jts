package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExJoinMpccRoom extends L2GameClientPacket {
    private int _roomId;

    @Override
    protected void readImpl() {
        _roomId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (player.getMatchingRoom() != null) {
            return;
        }

        final MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.CC_MATCHING, _roomId);
        if (room == null) {
            return;
        }

        room.addMember(player);
    }
}