package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 */
public class ExMpccRoomMember extends GameServerPacket {
    private final int type;
    private List<MpccRoomMemberInfo> members = Collections.emptyList();

    public ExMpccRoomMember(final MatchingRoom room, final Player player) {
        type = room.getMemberType(player);
        members = new ArrayList<>(room.getPlayers().size());

        for (final Player member : room.getPlayers()) {
            members.add(new MpccRoomMemberInfo(member, room.getMemberType(member)));
        }
    }

    @Override
    public void writeData() {
        writeD(type);
        writeD(members.size());
        for (final MpccRoomMemberInfo member : members) {
            writeD(member.objectId);
            writeS(member.name);
            writeD(member.level);
            writeD(member.classId);
            writeD(member.location);
            writeD(member.memberType);
        }
    }

    static class MpccRoomMemberInfo {
        public final int objectId;
        public final int classId;
        public final int level;
        public final int location;
        public final int memberType;
        public final String name;

        public MpccRoomMemberInfo(final Player member, final int type) {
            this.objectId = member.getObjectId();
            this.name = member.getName();
            this.classId = member.getPlayerClassComponent().getClassId().ordinal();
            this.level = member.getLevel();
            this.location = MatchingRoomManager.getInstance().getLocation(member);
            this.memberType = type;
        }
    }
}