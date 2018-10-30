package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeShowMemberListAdd extends GameServerPacket {
    private final PledgePacketMember member;

    public PledgeShowMemberListAdd(final UnitMember member) {
        this.member = new PledgePacketMember(member);
    }

    @Override
    protected final void writeData() {
        writeS(member.name);
        writeD(member.level);
        writeD(member.classId);
        writeD(member.sex);
        writeD(member.race);
        writeD(member.online);
        writeD(member.pledgeType);
    }

    private static class PledgePacketMember {
        private final String name;
        private final int level;
        private final int classId;
        private final int sex;
        private final int race;
        private final int online;
        private final int pledgeType;

        public PledgePacketMember(final UnitMember m) {
            name = m.getName();
            level = m.getLevel();
            classId = m.getClassId();
            sex = m.getSex();
            race = m.getRace();
            online = m.isOnline() ? m.getObjectId() : 0;
            pledgeType = m.getPledgeType();
        }
    }
}