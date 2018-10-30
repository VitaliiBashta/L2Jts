package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.RankPrivs;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeReceivePowerInfo extends GameServerPacket {
    private final int PowerGrade;
    private final int privs;
    private final String member_name;

    public PledgeReceivePowerInfo(final UnitMember member) {
        PowerGrade = member.getPowerGrade();
        member_name = member.getName();
        if (member.isClanLeader()) {
            privs = Clan.CP_ALL;
        } else {
            final RankPrivs temp = member.getClan().getRankPrivs(member.getPowerGrade());
            if (temp != null) {
                privs = temp.getPrivs();
            } else {
                privs = 0;
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(PowerGrade);
        writeS(member_name);
        writeD(privs);
    }
}