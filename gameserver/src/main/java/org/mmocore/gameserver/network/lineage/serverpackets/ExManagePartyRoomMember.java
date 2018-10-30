package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.matching.PartyMatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 * ddSddddd
 */
public class ExManagePartyRoomMember extends GameServerPacket {
    private final MpccRoomMemberInfo memberInfo;
    private final int mode;
    private final int state;

    public ExManagePartyRoomMember(final Player player, final PartyMatchingRoom partyRoom, final int mode) {
        memberInfo = (new MpccRoomMemberInfo(player, mode));
        this.mode = mode;
        if (partyRoom.getLeader().equals(player)) {
            state = 0x01;
        } else {
            if ((player.isInParty() && partyRoom.getLeader().isInParty()) && (player.getParty().getGroupLeader().getObjectId() == partyRoom.getLeader().getParty().getGroupLeader().getObjectId())) {
                state = 0x02;
            } else {
                state = 0x00;
            }
        }
    }

    @Override
    protected void writeData() {
        writeD(mode);
        writeD(memberInfo.objectId);
        writeS(memberInfo.name);
        writeD(memberInfo.classId);
        writeD(memberInfo.level);
        writeD(memberInfo.location);
        writeD(state);
        writeD(memberInfo.instanceReuses.length);
        for (final int i : memberInfo.instanceReuses) {
            writeD(i);
        }
    }

    static class MpccRoomMemberInfo {
        public final int objectId;
        public final int classId;
        public final int level;
        public final int location;
        public final int memberType;
        public final int[] instanceReuses;
        public final String name;

        public MpccRoomMemberInfo(final Player member, final int type) {
            this.objectId = member.getObjectId();
            this.name = member.getName();
            this.classId = member.getPlayerClassComponent().getClassId().ordinal();
            this.level = member.getLevel();
            this.location = MatchingRoomManager.getInstance().getLocation(member);
            this.memberType = type;
            this.instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
        }
    }
}