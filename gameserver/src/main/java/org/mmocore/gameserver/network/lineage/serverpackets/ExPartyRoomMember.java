package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Format:(ch) d d [dsdddd]
 */
public class ExPartyRoomMember extends GameServerPacket {
    private final int type;
    private List<PartyRoomMemberInfo> members = Collections.emptyList();

    public ExPartyRoomMember(final MatchingRoom room, final Player activeChar) {
        type = room.getMemberType(activeChar);
        members = new ArrayList<>(room.getPlayers().size());
        for (final Player $member : room.getPlayers()) {
            members.add(new PartyRoomMemberInfo($member, room.getMemberType($member)));
        }
    }

    @Override
    protected final void writeData() {
        writeD(type);
        writeD(members.size());
        for (final PartyRoomMemberInfo member_info : members) {
            writeD(member_info.objectId);
            writeS(member_info.name);
            writeD(member_info.classId);
            writeD(member_info.level);
            writeD(member_info.location);
            writeD(member_info.memberType);
            writeD(member_info.instanceReuses.length);
            for (final int i : member_info.instanceReuses) {
                writeD(i);
            }
        }
    }

    static class PartyRoomMemberInfo {
        public final int objectId, classId, level, location, memberType;
        public final String name;
        public final int[] instanceReuses;

        public PartyRoomMemberInfo(final Player member, final int type) {
            objectId = member.getObjectId();
            name = member.getName();
            classId = member.getPlayerClassComponent().getClassId().ordinal();
            level = member.getLevel();
            location = MatchingRoomManager.getInstance().getLocation(member);
            memberType = type;
            instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
        }
    }
}