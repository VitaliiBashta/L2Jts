package org.mmocore.gameserver.model.team.matching;

import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 0:44/12.06.2011
 */
public class CCMatchingRoom extends MatchingRoom {
    public CCMatchingRoom(final Player leader, final int minLevel, final int maxLevel, final int maxMemberSize, final int lootType, final String topic) {
        super(leader, minLevel, maxLevel, maxMemberSize, lootType, topic);
        leader.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED);
    }

    @Override
    public SystemMsg notValidMessage() {
        return SystemMsg.YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
    }

    @Override
    public SystemMsg enterMessage() {
        return SystemMsg.C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM;
    }

    @Override
    public SystemMsg exitMessage(final boolean toOthers, final boolean kick) {
        if (!toOthers) {
            return kick ? SystemMsg.YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM : SystemMsg.YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM;
        } else {
            return null;
        }
    }

    @Override
    public SystemMsg closeRoomMessage() {
        return SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED;
    }

    @Override
    public SystemMsg changeLeaderMessage() {
        return null;
    }

    @Override
    public L2GameServerPacket managerRoomPacket(Player $member, int mode) {
        return null;
    }

    @Override
    public L2GameServerPacket closeRoomPacket() {
        return ExDissmissMpccRoom.STATIC;
    }

    @Override
    public L2GameServerPacket infoRoomPacket() {
        return new ExMpccRoomInfo(this);
    }

    @Override
    public L2GameServerPacket addMemberPacket(final Player $member, final Player active) {
        return new ExManageMpccRoomMember(ExManageMpccRoomMember.ADD_MEMBER, this, active);
    }

    @Override
    public L2GameServerPacket removeMemberPacket(final Player $member, final Player active) {
        return new ExManageMpccRoomMember(ExManageMpccRoomMember.REMOVE_MEMBER, this, active);
    }

    @Override
    public L2GameServerPacket updateMemberPacket(final Player $member, final Player active) {
        return new ExManageMpccRoomMember(ExManageMpccRoomMember.UPDATE_MEMBER, this, active);
    }

    @Override
    public L2GameServerPacket membersPacket(final Player active) {
        return new ExMpccRoomMember(this, active);
    }

    @Override
    public int getType() {
        return CC_MATCHING;
    }

    @Override
    public void disband() {
        final Party party = _leader.getParty();
        if (party != null) {
            final CommandChannel commandChannel = party.getCommandChannel();
            if (commandChannel != null) {
                commandChannel.setMatchingRoom(null);
            }
        }
        super.disband();
    }

    @Override
    public int getMemberType(final Player member) {
        final Party party = _leader.getParty();
        final CommandChannel commandChannel = party.getCommandChannel();
        if (member == _leader) {
            return MatchingRoom.UNION_LEADER;
        } else if (member.getParty() == null) {
            return MatchingRoom.WAIT_NORMAL;
        } else if (member.getParty() == party || (commandChannel != null && commandChannel.getParties().contains(member.getParty()))) {
            return MatchingRoom.UNION_PARTY;
        } else if (member.getParty() != null) {
            return MatchingRoom.WAIT_PARTY;
        } else {
            return MatchingRoom.WAIT_NORMAL;
        }
    }
}