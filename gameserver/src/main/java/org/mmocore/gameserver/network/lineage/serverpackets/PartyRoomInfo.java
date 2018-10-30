package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PartyRoomInfo extends GameServerPacket {
    private final int id;
    private final int minLevel;
    private final int maxLevel;
    private final int lootDist;
    private final int maxMembers;
    private final int location;
    private final String title;

    public PartyRoomInfo(final MatchingRoom room) {
        id = room.getId();
        minLevel = room.getMinLevel();
        maxLevel = room.getMaxLevel();
        lootDist = room.getLootType();
        maxMembers = room.getMaxMembersSize();
        location = room.getLocationId();
        title = room.getTopic();
    }

    @Override
    protected final void writeData() {
        writeD(id); // room id
        writeD(maxMembers); //max members
        writeD(minLevel); //min level
        writeD(maxLevel); //max level
        writeD(lootDist); //loot distribution 1-Random 2-Random includ. etc
        writeD(location); //location
        writeS(title); // room name
    }
}