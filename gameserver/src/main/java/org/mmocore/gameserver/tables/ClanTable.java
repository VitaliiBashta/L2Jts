package org.mmocore.gameserver.tables;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListDeleteAll;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.SiegeUtils;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClanTable {
    private static final Logger _log = LoggerFactory.getLogger(ClanTable.class);

    private static ClanTable _instance;

    private final Map<Integer, Clan> _clans = new ConcurrentHashMap<>();
    private final Map<Integer, Alliance> _alliances = new ConcurrentHashMap<>();

    private ClanTable() {
        _instance = this;

        restoreClans();
        restoreAllies();
        restoreWars();
    }

    public static ClanTable getInstance() {
        if (_instance == null) {
            new ClanTable();
        }
        return _instance;
    }

    public Clan[] getClans() {
        return _clans.values().toArray(new Clan[_clans.size()]);
    }

    public Alliance[] getAlliances() {
        return _alliances.values().toArray(new Alliance[_alliances.size()]);
    }

    public Clan getClan(final int clanId) {
        if (clanId <= 0) {
            return null;
        }
        return _clans.get(clanId);
    }

    public String getClanName(final int clanId) {
        final Clan c = getClan(clanId);
        return c != null ? c.getName() : StringUtils.EMPTY;
    }

    public Clan getClanByCharId(final int charId) {
        if (charId <= 0) {
            return null;
        }
        for (final Clan clan : getClans()) {
            if (clan != null && clan.isAnyMember(charId)) {
                return clan;
            }
        }
        return null;
    }

    public Alliance getAlliance(final int allyId) {
        if (allyId <= 0) {
            return null;
        }
        return _alliances.get(allyId);
    }

    public Alliance getAllianceByCharId(final int charId) {
        if (charId <= 0) {
            return null;
        }
        final Clan charClan = getClanByCharId(charId);
        return charClan == null ? null : charClan.getAlliance();
    }

    public Map.Entry<Clan, Alliance> getClanAndAllianceByCharId(final int charId) {
        final Player player = GameObjectsStorage.getPlayer(charId);
        final Clan charClan = player != null ? player.getClan() : getClanByCharId(charId);
        return new SimpleEntry<>(charClan, charClan == null ? null : charClan.getAlliance());
    }

    public void restoreClans() {
        final List<Integer> clanIds = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT clan_id FROM clan_data");
            result = statement.executeQuery();
            while (result.next()) {
                clanIds.add(result.getInt("clan_id"));
            }
        } catch (Exception e) {
            _log.warn("Error while restoring clans!!! " + e);
        } finally {
            DbUtils.closeQuietly(con, statement, result);
        }

        for (final int clanId : clanIds) {
            final Clan clan = Clan.restore(clanId);
            if (clan == null) {
                _log.warn("Error while restoring clanId: " + clanId);
                continue;
            }

            if (clan.getAllSize() <= 0) {
                _log.warn("membersCount = 0 for clanId: {} - clanName: {}", clanId, clan.getName());
                continue;
            }

            if (clan.getLeader() == null) {
                _log.warn("Not found leader for clanId: {} - clanName: {}", clanId, clan.getName());
                continue;
            }

            _clans.put(clan.getClanId(), clan);
        }
    }

    public void restoreAllies() {
        final List<Integer> allyIds = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT ally_id FROM ally_data");
            result = statement.executeQuery();
            while (result.next()) {
                allyIds.add(result.getInt("ally_id"));
            }
        } catch (Exception e) {
            _log.warn("Error while restoring allies!!! " + e);
        } finally {
            DbUtils.closeQuietly(con, statement, result);
        }

        for (final int allyId : allyIds) {
            final Alliance ally = new Alliance(allyId);

            if (ally.getMembersCount() <= 0) {
                _log.warn("membersCount = 0 for allyId: " + allyId);
                continue;
            }

            if (ally.getLeader() == null) {
                _log.warn("Not found leader for allyId: " + allyId);
                continue;
            }

            _alliances.put(ally.getAllyId(), ally);
        }
    }

    public Clan getClanByName(final String clanName) {
        if (!Util.isMatchingRegexp(clanName, ServerConfig.CLAN_NAME_TEMPLATE)) {
            return null;
        }

        for (final Clan clan : _clans.values()) {
            if (clan.getName().equalsIgnoreCase(clanName)) {
                return clan;
            }
        }

        return null;
    }

    public int getClansSizeByName(final String clanName) {
        int result = 0;
        for (final Clan clan : _clans.values()) {
            if (clan.getName().equalsIgnoreCase(clanName)) {
                result++;
            }
        }

        return result;
    }

    public Alliance getAllyByName(final String allyName) {
        if (!Util.isMatchingRegexp(allyName, ServerConfig.ALLY_NAME_TEMPLATE)) {
            return null;
        }

        for (final Alliance ally : _alliances.values()) {
            if (ally.getAllyName().equalsIgnoreCase(allyName)) {
                return ally;
            }
        }

        return null;
    }

    public Clan createClan(final Player player, final String clanName) {
        if (getClanByName(clanName) == null) {
            final UnitMember leader = new UnitMember(player);
            leader.setLeaderOf(Clan.SUBUNIT_MAIN_CLAN);

            final Clan clan = new Clan(IdFactory.getInstance().getNextId());

            final SubUnit unit = new SubUnit(clan, Clan.SUBUNIT_MAIN_CLAN, leader, clanName);
            unit.addUnitMember(leader);
            clan.addSubUnit(unit, false);   //не нужно совать в базу. пихается ниже
            clan.setLevel(AllSettingsConfig.clanCreateLevel);

            clan.store();

            player.setPledgeType(Clan.SUBUNIT_MAIN_CLAN);
            player.setClan(clan);
            player.setPowerGrade(6);

            leader.setPlayerInstance(player, false);

            _clans.put(clan.getClanId(), clan);

            return clan;
        } else {
            return null;
        }
    }

    public void dissolveClan(final Player player) {
        final Clan clan = player.getClan();
        final long curtime = System.currentTimeMillis();
        SiegeUtils.removeSiegeSkills(player);
        for (final Player clanMember : clan.getOnlineMembers(0)) {
            clanMember.setClan(null);
            clanMember.setTitle(null);
            clanMember.sendPacket(PledgeShowMemberListDeleteAll.STATIC, SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN);
            clanMember.broadcastCharInfo();
            clanMember.setLeaveClanTime(curtime);
        }
        clan.flush();
        deleteClanFromDb(clan.getClanId());
        _clans.remove(clan.getClanId());
        player.sendPacket(SystemMsg.CLAN_HAS_DISPERSED);
        player.setDeleteClanTime(curtime);
    }

    public void deleteClanFromDb(final int clanId) {
        final long curtime = System.currentTimeMillis();

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(
                    "UPDATE characters SET clanid=0,title='',pledge_type=0,pledge_rank=0,lvl_joined_academy=0,apprentice=0,leaveclan=? WHERE clanid=?");
            statement.setLong(1, curtime / 1000L);
            statement.setInt(2, clanId);
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?");
            statement.setInt(1, clanId);
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?");
            statement.setInt(1, clanId);
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?");
            statement.setInt(1, clanId);
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?");
            statement.setInt(1, clanId);
            statement.execute();
        } catch (Exception e) {
            _log.warn("could not dissolve clan:" + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public Alliance createAlliance(final Player player, final String allyName) {
        Alliance alliance = null;

        if (getAllyByName(allyName) == null) {
            final Clan leader = player.getClan();
            alliance = new Alliance(IdFactory.getInstance().getNextId(), allyName, leader);
            alliance.store();
            _alliances.put(alliance.getAllyId(), alliance);

            player.getClan().setAllyId(alliance.getAllyId());
            for (final Player temp : player.getClan().getOnlineMembers(0)) {
                temp.broadcastCharInfo();
            }
        }

        return alliance;
    }

    public void dissolveAlly(final Player player) {
        final int allyId = player.getAllyId();
        for (final Clan member : player.getAlliance().getMembers()) {
            member.setAllyId(0);
            member.broadcastClanStatus(false, true, false);
            member.broadcastToOnlineMembers(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
            member.setLeavedAlly();
        }
        deleteAllyFromDb(allyId);
        _alliances.remove(allyId);
        player.sendPacket(SystemMsg.THE_ALLIANCE_HAS_BEEN_DISSOLVED);
        player.getClan().setDissolvedAlly();
    }

    public void deleteAllyFromDb(final int allyId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE ally_id=?");
            statement.setInt(1, allyId);
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM ally_data WHERE ally_id=?");
            statement.setInt(1, allyId);
            statement.execute();
        } catch (Exception e) {
            _log.warn("could not dissolve clan:" + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void startClanWar(final Clan clan1, final Clan clan2) {
        // clan1 is declaring war against clan2
        clan1.setEnemyClan(clan2);
        clan2.setAttackerClan(clan1);
        clan1.broadcastClanStatus(false, false, true);
        clan2.broadcastClanStatus(false, false, true);

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO clan_wars (clan1, clan2) VALUES(?,?)");
            statement.setInt(1, clan1.getClanId());
            statement.setInt(2, clan2.getClanId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("could not store clan war data:" + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        clan1.broadcastToOnlineMembers(new SystemMessage(SystemMsg.A_CLAN_WAR_HAS_BEEN_DECLARED_AGAINST_THE_CLAN_S1).addString(clan2.getName()));
        clan2.broadcastToOnlineMembers(new SystemMessage(SystemMsg.S1_HAS_DECLARED_A_CLAN_WAR).addString(clan1.getName()));
    }

    public void stopClanWar(final Clan clan1, final Clan clan2) {
        // clan1 is ceases war against clan2
        clan1.deleteEnemyClan(clan2);
        clan2.deleteAttackerClan(clan1);

        clan1.broadcastClanStatus(false, false, true);
        clan2.broadcastClanStatus(false, false, true);

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? AND clan2=?");
            statement.setInt(1, clan1.getClanId());
            statement.setInt(2, clan2.getClanId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("could not delete war data:" + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        clan1.broadcastToOnlineMembers(new SystemMessage(SystemMsg.THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED).addString(clan2.getName()));
        clan2.broadcastToOnlineMembers(new SystemMessage(SystemMsg.THE_CLAN_S1_HAS_DECIDED_TO_STOP_THE_WAR).addString(clan1.getName()));
    }

    private void restoreWars() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT clan1, clan2 FROM clan_wars");
            rset = statement.executeQuery();
            Clan clan1;
            Clan clan2;
            while (rset.next()) {
                clan1 = getClan(rset.getInt("clan1"));
                clan2 = getClan(rset.getInt("clan2"));
                if (clan1 != null && clan2 != null) {
                    clan1.setEnemyClan(clan2);
                    clan2.setAttackerClan(clan1);
                }
            }
        } catch (Exception e) {
            _log.warn("could not restore clan wars data:");
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }
}