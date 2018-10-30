package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 */
public class ExListMpccWaiting extends GameServerPacket {
    private static final int PAGE_SIZE = 10;
    private final int fullSize;
    private final List<MatchingRoom> list;

    public ExListMpccWaiting(final Player player, final int page, final int location, final boolean allLevels) {
        final int first = (page - 1) * PAGE_SIZE;
        final int firstNot = page * PAGE_SIZE;
        int i = 0;
        final Collection<MatchingRoom> all = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.CC_MATCHING, location, allLevels, player);
        fullSize = all.size();
        list = new ArrayList<>(PAGE_SIZE);
        for (final MatchingRoom c : all) {
            if (i < first || i >= firstNot) {
                continue;
            }

            list.add(c);
            i++;
        }
    }

    @Override
    public void writeData() {
        writeD(fullSize);
        writeD(list.size());
        for (final MatchingRoom room : list) {
            writeD(room.getId());
            writeS(room.getTopic());
            writeD(room.getPlayers().size());
            writeD(room.getMinLevel());
            writeD(room.getMaxLevel());
            writeD(1);  //min group
            writeD(room.getMaxMembersSize());   //max group
            final Player leader = room.getGroupLeader();
            writeS(leader == null ? StringUtils.EMPTY : leader.getName());
        }
    }
}
