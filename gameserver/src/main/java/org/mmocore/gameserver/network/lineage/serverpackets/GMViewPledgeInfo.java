package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GMViewPledgeInfo extends GameServerPacket {
    private final String playerName;
    private final String clan_name;
    private final String leader_name;
    private final String ally_name;
    private final int clan_id;
    private final int clan_crest_id;
    private final int clan_level;
    private final int rank;
    private final int rep;
    private final int ally_id;
    private final int ally_crest_id;
    private final int hasCastle;
    private final int hasHideout;
    private final int hasFortress;
    private final int atWar;
    private final int territorySide;
    private final int pledgeType;
    private List<PledgeMemberInfo> members = Collections.emptyList();

    public GMViewPledgeInfo(final String name, final Clan clan, final SubUnit subUnit) {
        members = new ArrayList<>(subUnit.getUnitMembers().size());

        pledgeType = subUnit.getType();
        playerName = name;
        clan_id = clan.getClanId();
        clan_name = clan.getName();
        leader_name = clan.getLeaderName();
        clan_crest_id = clan.getCrestId();
        clan_level = clan.getLevel();
        hasCastle = clan.getCastle();
        hasHideout = clan.getHasHideout();
        hasFortress = clan.getHasFortress();
        rank = clan.getRank();
        rep = clan.getReputationScore();
        territorySide = clan.getWarDominion();
        final Alliance alliance = clan.getAlliance();
        ally_id = alliance == null ? 0 : alliance.getAllyId();
        ally_name = alliance == null ? StringUtils.EMPTY : alliance.getAllyName();
        ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
        atWar = clan.isAtWar();

        for (final UnitMember member : subUnit.getUnitMembers()) {
            members.add(new PledgeMemberInfo(member.getName(), member.getLevel(), member.getClassId(), member.isOnline() ? member.getObjectId() : 0, member.getSex(), 1, member.getSponsor() != 0 ? 1 : 0));
        }
    }

    @Override
    protected final void writeData() {
        writeD(pledgeType == Clan.SUBUNIT_MAIN_CLAN);
        writeS(playerName);
        writeD(clan_id);
        writeD(pledgeType);
        writeS(clan_name);
        writeS(leader_name);
        writeD(clan_crest_id);
        writeD(clan_level);
        writeD(hasCastle);
        writeD(hasHideout);
        writeD(hasFortress);
        writeD(rank);
        writeD(rep);
        writeD(0);
        writeD(0);
        writeD(ally_id);
        writeS(ally_name);
        writeD(ally_crest_id);
        writeD(atWar);
        writeD(territorySide);
        writeD(members.size());
        for (final PledgeMemberInfo info : members) {
            writeS(info.name);
            writeD(info.level);
            writeD(info.class_id);
            writeD(info.sex);
            writeD(info.race);
            writeD(info.online);
            writeD(info.sponsor);
        }
    }

    static class PledgeMemberInfo {
        public final String name;
        public final int level;
        public final int class_id;
        public final int online;
        public final int sex;
        public final int race;
        public final int sponsor;

        public PledgeMemberInfo(final String name, final int level, final int class_id, final int online, final int sex, final int race, final int sponsor) {
            this.name = name;
            this.level = level;
            this.class_id = class_id;
            this.online = online;
            this.sex = sex;
            this.race = race;
            this.sponsor = sponsor;
        }
    }
}