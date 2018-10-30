package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class ExManageMpccRoomMember extends GameServerPacket {
    public static final int ADD_MEMBER = 0;
    public static final int UPDATE_MEMBER = 1;
    public static final int REMOVE_MEMBER = 2;

    private final int type;
    private final MpccRoomMemberInfo memberInfo;

    public ExManageMpccRoomMember(final int type, final MatchingRoom room, final Player target) {
        this.type = type;
        memberInfo = (new MpccRoomMemberInfo(target, room.getMemberType(target)));
    }

    @Override
    protected void writeData() {
        writeD(type);
        writeD(memberInfo.objectId);
        writeS(memberInfo.name);
        writeD(memberInfo.level);
        writeD(memberInfo.classId);
        writeD(memberInfo.location);
        writeD(memberInfo.memberType);
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