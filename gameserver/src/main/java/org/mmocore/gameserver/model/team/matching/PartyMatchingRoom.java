package org.mmocore.gameserver.model.team.matching;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 0:44/12.06.2011
 */
public class PartyMatchingRoom extends MatchingRoom {
    public PartyMatchingRoom(final Player leader, final int minLevel, final int maxLevel, final int maxMemberSize, final int lootType, final String topic) {
        super(leader, minLevel, maxLevel, maxMemberSize, lootType, topic);
        leader.broadcastCharInfo();
    }

    @Override
    public SystemMsg notValidMessage() {
        return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM;
    }

    @Override
    public SystemMsg enterMessage() {
        return SystemMsg.C1_HAS_ENTERED_THE_PARTY_ROOM;
    }

    @Override
    public SystemMsg exitMessage(final boolean toOthers, final boolean kick) {
        if (toOthers) {
            return kick ? SystemMsg.C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM : SystemMsg.C1_HAS_LEFT_THE_PARTY_ROOM;
        } else {
            return kick ? SystemMsg.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMsg.YOU_HAVE_EXITED_THE_PARTY_ROOM;
        }
    }

    @Override
    public SystemMsg closeRoomMessage() {
        return SystemMsg.THE_PARTY_ROOM_HAS_BEEN_DISBANDED;
    }

    @Override
    public SystemMsg changeLeaderMessage() {
        return SystemMsg.THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED;
    }

    @Override
    public L2GameServerPacket closeRoomPacket() {
        return ExClosePartyRoom.STATIC;
    }

    @Override
    public L2GameServerPacket infoRoomPacket() {
        return new PartyRoomInfo(this);
    }

    @Override
    public L2GameServerPacket addMemberPacket(final Player $member, final Player active) {
        return membersPacket($member);
    }

    @Override
    public L2GameServerPacket removeMemberPacket(final Player $member, final Player active) {
        return membersPacket($member);
    }

    @Override
    public L2GameServerPacket updateMemberPacket(final Player $member, final Player active) {
        return membersPacket($member);
    }

    @Override
    public L2GameServerPacket managerRoomPacket(final Player $member, final int mode) {
        return new ExManagePartyRoomMember($member, this, mode);
    }

    @Override
    public L2GameServerPacket membersPacket(final Player active) {
        return new ExPartyRoomMember(this, active);
    }

    @Override
    public int getType() {
        return PARTY_MATCHING;
    }

    @Override
    public int getMemberType(final Player member) {
        return member.equals(_leader) ? ROOM_MASTER : member.getParty() != null && _leader.getParty() == member.getParty() ? PARTY_MEMBER : WAIT_PLAYER;
    }
}