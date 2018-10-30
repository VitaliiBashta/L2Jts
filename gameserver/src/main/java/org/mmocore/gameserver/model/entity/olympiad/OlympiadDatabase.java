package org.mmocore.gameserver.model.entity.olympiad;

import gnu.trove.map.hash.TIntIntHashMap;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.OlympiadNobleDAO;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OlympiadDatabase {
    private static final Logger _log = LoggerFactory.getLogger(OlympiadDatabase.class);

    public static synchronized void loadNoblesRank() {
        Olympiad.noblesRank = new TIntIntHashMap();
        final Map<Integer, Integer> tmpPlace = new HashMap<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(OlympiadNobleDAO.GET_ALL_CLASSIFIED_NOBLESS);
            rset = statement.executeQuery();
            int place = 1;
            while (rset.next()) {
                tmpPlace.put(rset.getInt(Olympiad.CHAR_ID), place++);
            }

        } catch (Exception e) {
            _log.error("Olympiad System: Error!", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        int rank1 = (int) Math.round(tmpPlace.size() * 0.01);
        int rank2 = (int) Math.round(tmpPlace.size() * 0.10);
        int rank3 = (int) Math.round(tmpPlace.size() * 0.25);
        int rank4 = (int) Math.round(tmpPlace.size() * 0.50);

        if (rank1 == 0) {
            rank1 = 1;
            rank2++;
            rank3++;
            rank4++;
        }

        for (final int charId : tmpPlace.keySet()) {
            if (tmpPlace.get(charId) <= rank1) {
                Olympiad.noblesRank.put(charId, 1);
            } else if (tmpPlace.get(charId) <= rank2) {
                Olympiad.noblesRank.put(charId, 2);
            } else if (tmpPlace.get(charId) <= rank3) {
                Olympiad.noblesRank.put(charId, 3);
            } else if (tmpPlace.get(charId) <= rank4) {
                Olympiad.noblesRank.put(charId, 4);
            } else {
                Olympiad.noblesRank.put(charId, 5);
            }
        }
    }

    /**
     * Сбрасывает информацию о ноблесах, сохраняя очки за предыдущий период
     */
    public static synchronized void cleanupNobles() {
        _log.info("Olympiad: Calculating last period...");
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(OlympiadNobleDAO.OLYMPIAD_CALCULATE_LAST_PERIOD);
            statement.setInt(1, OlympiadConfig.OLYMPIAD_BATTLES_FOR_REWARD);
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement(OlympiadNobleDAO.OLYMPIAD_CLEANUP_NOBLES);
            statement.setInt(1, OlympiadConfig.OLYMPIAD_POINTS_DEFAULT);
            statement.execute();
        } catch (Exception e) {
            _log.error("Olympiad System: Couldn't calculate last period!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        for (final Integer nobleId : Olympiad.nobles.keySet()) {
            final StatsSet nobleInfo = Olympiad.nobles.get(nobleId);
            final int points = nobleInfo.getInteger(Olympiad.POINTS);
            final int compDone = nobleInfo.getInteger(Olympiad.COMP_DONE);
            nobleInfo.set(Olympiad.POINTS, OlympiadConfig.OLYMPIAD_POINTS_DEFAULT);
            if (compDone >= OlympiadConfig.OLYMPIAD_BATTLES_FOR_REWARD) {
                nobleInfo.set(Olympiad.POINTS_PAST, points);
                nobleInfo.set(Olympiad.POINTS_PAST_STATIC, points);
            } else {
                nobleInfo.set(Olympiad.POINTS_PAST, 0);
                nobleInfo.set(Olympiad.POINTS_PAST_STATIC, 0);
            }
            nobleInfo.set(Olympiad.COMP_DONE, 0);
            nobleInfo.set(Olympiad.COMP_WIN, 0);
            nobleInfo.set(Olympiad.COMP_LOOSE, 0);
            nobleInfo.set(Olympiad.GAME_CLASSES_COUNT, 0);
            nobleInfo.set(Olympiad.GAME_NOCLASSES_COUNT, 0);
            nobleInfo.set(Olympiad.GAME_TEAM_COUNT, 0);
        }
    }

    public static List<String> getClassLeaderBoard(final int classId) {
        final List<String> names = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(classId == 132 ? OlympiadNobleDAO.GET_EACH_CLASS_LEADER_SOULHOUND : OlympiadNobleDAO.GET_EACH_CLASS_LEADER);
            statement.setInt(1, classId);
            rset = statement.executeQuery();
            while (rset.next()) {
                names.add(rset.getString(Olympiad.CHAR_NAME));
            }
        } catch (Exception e) {
            _log.error("Olympiad System: Couldnt get heros from db!", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return names;
    }

    public static synchronized void sortHerosToBe() {
        if (Olympiad._period != 1) {
            return;
        }

        Olympiad.heroesToBe = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            StatsSet hero;

            for (final ClassId id : ClassId.VALUES) {
                if (id.getId() == 133) {
                    continue;
                }
                if (id.level() == 3) {
                    statement = con.prepareStatement(id.getId() == 132 ? OlympiadNobleDAO.OLYMPIAD_GET_HEROS_SOULHOUND : OlympiadNobleDAO.OLYMPIAD_GET_HEROS);
                    statement.setInt(1, id.getId());
                    statement.setInt(2, 15);
                    rset = statement.executeQuery();

                    if (rset.next()) {
                        hero = new StatsSet();
                        hero.set(Olympiad.CLASS_ID, id.getId());
                        final int charId = rset.getInt(Olympiad.CHAR_ID);
                        hero.set(Olympiad.CHAR_ID, charId);
                        final String charName = rset.getString(Olympiad.CHAR_NAME);
                        hero.set(Olympiad.CHAR_NAME, charName);

                        // FIXME: тупой костыль
                        if (charName == null || charName.isEmpty())
                            _log.error("Can't find player with id {}", charId);
                        else
                            Olympiad.heroesToBe.add(hero);
                    }
                    DbUtils.close(statement, rset);
                }
            }
        } catch (Exception e) {
            _log.error("Olympiad System: Couldnt heros from db!", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public static synchronized void saveNobleData(final int nobleId) {
        OlympiadNobleDAO.getInstance().replace(nobleId);
    }

    public static synchronized void saveNobleData() {
        if (Olympiad.nobles == null) {
            return;
        }
        for (final Integer nobleId : Olympiad.nobles.keySet()) {
            saveNobleData(nobleId);
        }
    }

    public static synchronized void setNewOlympiadEnd() {
        AnnouncementUtils.announceToAll(new SystemMessage(SystemMsg.ROUND_S1_OF_THE_GRAND_OLYMPIAD_GAMES_HAS_STARTED).addNumber(Olympiad._currentCycle));
        long periodMills;
        if (OlympiadConfig.OLYMPIAD_USE_WEEKLY_PERIOD) {
            ZonedDateTime nextTime = ZonedDateTime.now(ZoneId.systemDefault())
                    .with(ChronoField.DAY_OF_WEEK, OlympiadConfig.OLYMPIAD_DAY_OF_WEEK);
            if (nextTime.isAfter(ZonedDateTime.now(ZoneId.systemDefault())))
                periodMills = nextTime.toInstant().toEpochMilli();
            else
                periodMills = nextTime.toLocalDate().atStartOfDay(ZoneId.systemDefault())
                        .plusWeeks(OlympiadConfig.OLYMPIAD_WEEK_COUNT)
                        .with(ChronoField.DAY_OF_WEEK, OlympiadConfig.OLYMPIAD_DAY_OF_WEEK)
                        .toInstant().toEpochMilli();
        } else
            periodMills = ZonedDateTime.now(ZoneId.systemDefault())
                    .toLocalDate().atStartOfDay(ZoneId.systemDefault())
                    .with(ChronoField.DAY_OF_MONTH, 1)
                    .plusMonths(1)
                    .toInstant().toEpochMilli();
        Olympiad._olympiadEnd = periodMills;
        Olympiad._nextWeeklyChange = System.currentTimeMillis() + OlympiadConfig.ALT_OLY_WPERIOD;
        Olympiad._isOlympiadEnd = false;
    }

    public static void save() {
        saveNobleData();
        ServerVariables.set("Olympiad_CurrentCycle", Olympiad._currentCycle);
        ServerVariables.set("Olympiad_Period", Olympiad._period);
        ServerVariables.set("Olympiad_End", Olympiad._olympiadEnd);
        ServerVariables.set("Olympiad_ValdationEnd", Olympiad._validationEnd);
        ServerVariables.set("Olympiad_NextWeeklyChange", Olympiad._nextWeeklyChange);
    }
}