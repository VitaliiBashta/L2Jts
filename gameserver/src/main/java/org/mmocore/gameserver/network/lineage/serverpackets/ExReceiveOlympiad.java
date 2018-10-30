package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadGame;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadManager;
import org.mmocore.gameserver.model.entity.olympiad.TeamMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 0:50/09.04.2011
 */
public abstract class ExReceiveOlympiad extends L2GameServerPacket {
    private final int type;

    public ExReceiveOlympiad(final int type) {
        this.type = type;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xD4);
        writeD(type);
    }

    public static class MatchList extends ExReceiveOlympiad {
        private List<ArenaInfo> arenaList = Collections.emptyList();

        public MatchList() {
            super(0);
            final OlympiadManager manager = Olympiad._manager;
            if (manager != null) {
                arenaList = new ArrayList<>();
                for (int i = 0; i < Olympiad.STADIUMS.length; i++) {
                    final OlympiadGame game = manager.getOlympiadInstance(i);
                    if (game != null && game.getState() > 0) {
                        arenaList.add(new ArenaInfo(i, game.getState(), game.getType().ordinal(), game.getTeamName1(), game.getTeamName2()));
                    }
                }
            }
        }

        @Override
        protected void writeImpl() {
            super.writeImpl();
            writeD(arenaList.size());
            writeD(0x00); //unknown
            for (final ArenaInfo arena : arenaList) {
                writeD(arena.id);
                writeD(arena.matchType);
                writeD(arena.status);
                writeS(arena.name1);
                writeS(arena.name2);
            }
        }

        private static class ArenaInfo {
            private final int id;
            private final int status;
            private final int matchType;
            private final String name1;
            private final String name2;

            public ArenaInfo(final int id, final int status, final int match_type, final String name1, final String name2) {
                this.id = id;
                this.status = status;
                matchType = match_type;
                this.name1 = name1;
                this.name2 = name2;
            }
        }
    }

    public static class MatchResult extends ExReceiveOlympiad {
        @SuppressWarnings("unchecked")
        private final List<PlayerInfo>[] players = new ArrayList[2];
        private final boolean tie;
        private final String name;

        public MatchResult(final boolean tie, final String name) {
            super(1);
            this.tie = tie;
            this.name = name;
        }

        public void addPlayer(final int team, final TeamMember member, final int gameResultPoints) {
            final int points = OlympiadConfig.OLYMPIAD_OLDSTYLE_STAT ? 0 : member.getStat().getInteger(Olympiad.POINTS, 0);

            final PlayerInfo playerInfo = new PlayerInfo(member.getName(), member.getClanName(), member.getClassId(), points, gameResultPoints, (int) member.getDamage());
            if (players[team] == null) {
                players[team] = new ArrayList<>(2);
            }

            players[team].add(playerInfo);
        }


        @Override
        protected void writeImpl() {
            super.writeImpl();
            writeD(tie);
            writeS(name);
            for (int i = 0; i < players.length; i++) {
                writeD(i + 1);
                final List<PlayerInfo> players = this.players[i] == null ? Collections.<PlayerInfo>emptyList() : this.players[i];
                writeD(players.size());
                for (final PlayerInfo playerInfo : players) {
                    writeS(playerInfo.name);
                    writeS(playerInfo.clanName);
                    writeD(0x00);
                    writeD(playerInfo.classId);
                    writeD(playerInfo.damage);
                    writeD(playerInfo.currentPoints);
                    writeD(playerInfo.gamePoints);
                }
            }
        }

        private static class PlayerInfo {
            private final String name;
            private final String clanName;
            private final int classId;
            private final int currentPoints;
            private final int gamePoints;
            private final int damage;

            public PlayerInfo(final String name, final String clanName, final int classId, final int currentPoints, final int gamePoints, final int damage) {
                this.name = name;
                this.clanName = clanName;
                this.classId = classId;
                this.currentPoints = currentPoints;
                this.gamePoints = gamePoints;
                this.damage = damage;
            }
        }
    }
}
