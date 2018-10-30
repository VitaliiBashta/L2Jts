package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;


public class PledgeShowMemberListAll extends GameServerPacket {
    private final int clanObjectId;
    private final int clanCrestId;
    private final int level;
    private final int rank;
    private final int reputation;
    private final int hasCastle;
    private final int hasClanHall;
    private final int hasFortress;
    private final int atClanWar;
    private final String unitName;
    private final String leaderName;
    private final int pledgeType;
    private final int territorySide;
    private final List<PledgePacketMember> members;
    private int allianceObjectId;
    private int allianceCrestId;
    private String allianceName;

    public PledgeShowMemberListAll(final Clan clan, final SubUnit sub) {
        pledgeType = sub.getType();
        clanObjectId = clan.getClanId();
        unitName = sub.getName();
        leaderName = sub.getLeaderName();
        clanCrestId = clan.getCrestId();
        level = clan.getLevel();
        hasCastle = clan.getCastle();
        hasClanHall = clan.getHasHideout();
        hasFortress = clan.getHasFortress();
        rank = clan.getRank();
        reputation = clan.getReputationScore();
        atClanWar = clan.isAtWarOrUnderAttack();
        territorySide = clan.getWarDominion();

        final Alliance ally = clan.getAlliance();

        if (ally != null) {
            allianceObjectId = ally.getAllyId();
            allianceName = ally.getAllyName();
            allianceCrestId = ally.getAllyCrestId();
        }

        members = new ArrayList<>(sub.size());

        for (final UnitMember m : sub.getUnitMembers()) {
            members.add(new PledgePacketMember(m));
        }
    }

    @Override
    protected final void writeData() {
        writeD(pledgeType == Clan.SUBUNIT_MAIN_CLAN);
        writeD(clanObjectId);
        writeD(pledgeType);
        writeS(unitName);
        writeS(leaderName);
        writeD(clanCrestId); // crest id .. is used again
        writeD(level);
        writeD(hasCastle);
        writeD(hasClanHall);
        writeD(hasFortress);
        writeD(rank);
        writeD(reputation);
        writeD(0x00);
        writeD(0x00);
        writeD(allianceObjectId);
        writeS(allianceName);
        writeD(allianceCrestId);
        writeD(atClanWar);
        writeD(territorySide);//territory Id

        writeD(members.size());
        for (final PledgePacketMember m : members) {
            writeS(m.name);
            writeD(m.level);
            writeD(m.classId);
            writeD(m.sex);
            writeD(m.race);
            writeD(m.online);
            writeD(m.hasSponsor ? 1 : 0);
        }
    }

    private static class PledgePacketMember {
        private final String name;
        private final int level;
        private final int classId;
        private final int sex;
        private final int race;
        private final int online;
        private final boolean hasSponsor;

        public PledgePacketMember(final UnitMember m) {
            name = m.getName();
            level = m.getLevel();
            classId = m.getClassId();
            sex = m.getSex();
            race = m.getRace();
            online = m.isOnline() ? m.getObjectId() : 0;
            hasSponsor = m.getSponsor() != 0;
        }
    }
}