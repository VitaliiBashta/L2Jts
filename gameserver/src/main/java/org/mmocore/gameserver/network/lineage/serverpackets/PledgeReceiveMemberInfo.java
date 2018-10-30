package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeReceiveMemberInfo extends GameServerPacket {
    private final UnitMember member;

    public PledgeReceiveMemberInfo(final UnitMember member) {
        this.member = member;
    }

    @Override
    protected final void writeData() {
        writeD(member.getPledgeType());
        writeS(member.getName());
        writeS(member.getTitle());
        writeD(member.getPowerGrade());
        writeS(member.getSubUnit().getName());
        writeS(member.getRelatedName()); // apprentice/sponsor name if any
    }
}