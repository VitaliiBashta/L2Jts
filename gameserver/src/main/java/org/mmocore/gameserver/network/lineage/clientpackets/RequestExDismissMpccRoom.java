package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExDismissMpccRoom extends L2GameClientPacket {
    @Override
    protected void readImpl() {

    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final MatchingRoom room = player.getMatchingRoom();
        if (room == null || room.getType() != MatchingRoom.CC_MATCHING) {
            return;
        }

        if (room.getGroupLeader() != player) {
            return;
        }

        room.disband();
    }
}