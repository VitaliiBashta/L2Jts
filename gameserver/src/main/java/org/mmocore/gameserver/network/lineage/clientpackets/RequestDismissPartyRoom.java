package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.object.Player;

/**
 * Format: (ch) dd
 */
public class RequestDismissPartyRoom extends L2GameClientPacket {
    private int _roomId;

    @Override
    protected void readImpl() {
        _roomId = readD(); //room id
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final MatchingRoom room = player.getMatchingRoom();
        if (room.getId() != _roomId || room.getType() != MatchingRoom.PARTY_MATCHING) {
            return;
        }

        if (room.getGroupLeader() != player) {
            return;
        }

        room.disband();
    }
}