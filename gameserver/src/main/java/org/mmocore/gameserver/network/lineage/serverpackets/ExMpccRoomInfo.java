package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExMpccRoomInfo extends GameServerPacket {
    private final int index;
    private final int memberSize;
    private final int minLevel;
    private final int maxLevel;
    private final int lootType;
    private final int locationId;
    private final String topic;

    public ExMpccRoomInfo(final MatchingRoom matching) {
        index = matching.getId();
        locationId = matching.getLocationId();
        topic = matching.getTopic();
        minLevel = matching.getMinLevel();
        maxLevel = matching.getMaxLevel();
        memberSize = matching.getMaxMembersSize();
        lootType = matching.getLootType();
    }

    @Override
    public void writeData() {
        writeD(index); //index
        writeD(memberSize); // member size 1-50
        writeD(minLevel); //min level
        writeD(maxLevel); //max level
        writeD(lootType); //loot type
        writeD(locationId); //location id as party room
        writeS(topic); //topic
    }
}