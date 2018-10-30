package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Format:(c) dddddds
 */
public class ListPartyWaiting extends GameServerPacket {
    private final Collection<MatchingRoom> rooms;
    private final int fullSize;

    public ListPartyWaiting(final int region, final boolean allLevels, final int page, final Player activeChar) {
        final int first = (page - 1) * 64;
        final int firstNot = page * 64;
        rooms = new ArrayList<>();

        int i = 0;
        final List<MatchingRoom> temp = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, region, allLevels, activeChar);
        fullSize = temp.size();
        for (final MatchingRoom room : temp) {
            if (i < first || i >= firstNot) {
                continue;
            }
            rooms.add(room);
            i++;
        }
    }

    @Override
    protected final void writeData() {
        writeD(fullSize);
        writeD(rooms.size());

        for (final MatchingRoom room : rooms) {
            writeD(room.getId()); //room id
            writeS(room.getTopic()); // room name
            writeD(room.getLocationId());
            writeD(room.getMinLevel()); //min level
            writeD(room.getMaxLevel()); //max level
            writeD(room.getMaxMembersSize()); //max members coun
            writeS(room.getGroupLeader() == null ? "None" : room.getGroupLeader().getName());

            final Collection<Player> players = room.getPlayers();
            writeD(players.size()); //members count
            for (final Player player : players) {
                writeD(player.getPlayerClassComponent().getClassId().getId());
                writeS(player.getName());
            }
        }
    }
}