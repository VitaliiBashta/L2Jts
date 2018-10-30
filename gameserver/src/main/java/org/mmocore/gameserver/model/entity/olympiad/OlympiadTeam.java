package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.ExOlympiadUserInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OlympiadTeam {
    private final OlympiadGame _game;
    private final Map<Integer, TeamMember> members;
    private final int _side;
    private String _name = "";
    private double _damage;

    public OlympiadTeam(final OlympiadGame game, final int side) {
        _game = game;
        _side = side;
        members = new ConcurrentHashMap<>();
    }

    public void addMember(final int obj_id) {
        String player_name = "";
        final Player player = GameObjectsStorage.getPlayer(obj_id);
        if (player != null) {
            player_name = player.getName();
        } else {
            final StatsSet noble = Olympiad.nobles.get(obj_id);
            if (noble != null) {
                player_name = noble.getString(Olympiad.CHAR_NAME, "");
            }
        }

        members.put(obj_id, new TeamMember(obj_id, player_name, player, _game, _side));

        _name = player_name;
    }

    public void addDamage(final Player player, final double damage) {
        _damage += damage;

        final TeamMember member = members.get(player.getObjectId());
        member.addDamage(damage);
    }

    public double getDamage() {
        return _damage;
    }

    public String getName() {
        return _name;
    }

    public void portPlayersToArena() {
        members.values().forEach(TeamMember::portPlayerToArena);
    }

    public void portPlayersBack() {
        members.values().forEach(TeamMember::portPlayerBack);
    }

    public void heal() {
        members.values().forEach(TeamMember::heal);
    }

    public void revive() {
        members.values().forEach(TeamMember::revive);
    }

    public void removeBuffs(final boolean fromSummon) {
        for (final TeamMember member : members.values()) {
            member.removeBuffs(fromSummon);
        }
    }

    public void preparePlayers() {
        members.values().forEach(TeamMember::preparePlayer);

        if (members.size() <= 1) {
            return;
        }

        final List<Player> list = new ArrayList<>();
        for (final TeamMember member : members.values()) {
            final Player player = member.getPlayer();
            if (player != null) {
                list.add(player);
                player.leaveParty();
            }
        }

        if (list.size() <= 1) {
            return;
        }

        final Player leader = list.get(0);
        if (leader == null) {
            return;
        }

        final Party party = new Party(leader, 0);
        leader.setParty(party);

        list.stream().filter(player -> player != leader).forEach(player -> {
            player.joinParty(party);
        });
    }

    public void takePointsForCrash() {
        members.values().forEach(TeamMember::takePointsForCrash);
    }

    public boolean checkPlayers() {
        for (final TeamMember member : members.values()) {
            if (!member.checkPlayer()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllDead() {
        for (final TeamMember member : members.values()) {
            if (!member.isDead() && member.checkPlayer()) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(final int objId) {
        return members.containsKey(objId);
    }

    public List<Player> getPlayers() {
        final List<Player> players = new ArrayList<>(members.size());
        for (final TeamMember member : members.values()) {
            final Player player = member.getPlayer();
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public Collection<TeamMember> getMembers() {
        return members.values();
    }

    public void broadcast(final L2GameServerPacket p) {
        for (final TeamMember member : members.values()) {
            final Player player = member.getPlayer();
            if (player != null) {
                player.sendPacket(p);
            }
        }
    }

    public void broadcast(final IBroadcastPacket p) {
        for (final TeamMember member : members.values()) {
            final Player player = member.getPlayer();
            if (player != null) {
                player.sendPacket(p);
            }
        }
    }

    public void broadcastInfo() {
        for (final TeamMember member : members.values()) {
            final Player player = member.getPlayer();
            if (player != null) {
                player.broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
            }
        }
    }

    public boolean logout(final Player player) {
        if (player != null) {
            for (final TeamMember member : members.values()) {
                final Player pl = member.getPlayer();
                if (pl != null && pl == player) {
                    member.logout();
                }
            }
        }
        return checkPlayers();
    }

    public boolean doDie(final Player player) {
        if (player != null) {
            for (final TeamMember member : members.values()) {
                final Player pl = member.getPlayer();
                if (pl != null && pl == player) {
                    member.doDie();
                }
            }
        }
        return isAllDead();
    }

    public void saveNobleData() {
        members.values().forEach(TeamMember::saveNobleData);
    }
}