package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import org.mmocore.gameserver.model.entity.events.objects.UCMemberObject;
import org.mmocore.gameserver.model.entity.events.objects.UCTeamObject;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;

public class ExPVPMatchRecord extends GameServerPacket {
    public static final int START = 0;
    public static final int UPDATE = 1;
    public static final int FINISH = 2;
    private final int type;
    private final TeamType winnerTeam;
    private final int blueKills;
    private final int redKills;
    private final List<Member> blueList;
    private final List<Member> redList;
    public ExPVPMatchRecord(int type, TeamType winnerTeam, UndergroundColiseumBattleEvent battleEvent) {
        this.type = type;
        this.winnerTeam = winnerTeam;

        final UCTeamObject blueTeam = battleEvent.getFirstObject(TeamType.BLUE);
        blueKills = blueTeam.getKills();
        final UCTeamObject redTeam = battleEvent.getFirstObject(TeamType.RED);
        redKills = redTeam.getKills();

        blueList = new ArrayList<Member>(9);
        for (UCMemberObject memberObject : blueTeam) {
            if (memberObject != null) {
                blueList.add(new Member(memberObject.getName(), memberObject.getKills(), memberObject.getDeaths()));
            }
        }

        redList = new ArrayList<Member>(9);
        for (UCMemberObject memberObject : redTeam) {
            if (memberObject != null) {
                redList.add(new Member(memberObject.getName(), memberObject.getKills(), memberObject.getDeaths()));
            }
        }
    }

    public ExPVPMatchRecord(int type, TeamType winnerTeam, int blueKills, int redKills, List<Member> blueTeam, List<Member> redTeam) {
        this.type = type;
        this.winnerTeam = winnerTeam;
        this.blueKills = blueKills;
        this.redKills = redKills;
        blueList = blueTeam;
        redList = redTeam;
    }

    @Override
    protected void writeData() {
        writeD(type);
        writeD(winnerTeam.ordinal());
        writeD(winnerTeam.revert().ordinal());
        writeD(blueKills);
        writeD(redKills);
        writeD(blueList.size());
        for (Member member : blueList) {
            writeS(member.name);
            writeD(member.kills);
            writeD(member.deaths);
        }
        writeD(redList.size());
        for (Member member : redList) {
            writeS(member.name);
            writeD(member.kills);
            writeD(member.deaths);
        }
    }

    public static class Member {
        public final String name;
        public final int kills;
        public final int deaths;

        public Member(String name, int kills, int deaths) {
            this.name = name;
            this.kills = kills;
            this.deaths = deaths;
        }
    }
}