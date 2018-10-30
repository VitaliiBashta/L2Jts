package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.serverpackets.ExMpccPartymasterList;
import org.mmocore.gameserver.object.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 */
public class RequestExMpccPartymasterList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //
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

        final Set<String> set = new HashSet<>();
        for (final Player $member : room.getPlayers()) {
            if ($member.getParty() != null) {
                set.add($member.getParty().getGroupLeader().getName());
            }
        }

        player.sendPacket(new ExMpccPartymasterList(set));
    }
}