package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author VISTALL
 */
public class RequestExOustFromMpccRoom extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
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

        final Player member = GameObjectsStorage.getPlayer(_objectId);
        if (member == null) {
            return;
        }

        if (member == room.getGroupLeader()) {
            return;
        }

        room.removeMember(member, true);
    }
}