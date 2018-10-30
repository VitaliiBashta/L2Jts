package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.BotReportManager;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

/**
 * @author KilRoy
 * <p>
 * TODO: убрать логику отсюда нахер.
 */
public class BotReportDAO {
    private static final Logger logger = LoggerFactory.getLogger(BotReportDAO.class);
    private static final BotReportDAO instance = new BotReportDAO();

    private static final String SQL_QUERY_SELECT_PUNISH = "SELECT `punish_type`, `time_left` FROM `bot_reported_punish` WHERE `charId` = ?";
    private static final String SQL_QUERY_DELETE_PUNISH = "DELETE FROM bot_reported_punish WHERE charId = ?";
    private static final String SQL_QUERY_UPDATE_PUNISH = "UPDATE `bot_reported_punish` SET `time_left` = ? WHERE `charId` = ?";
    private static final String SQL_QUERY_SELECT_PUNISH_COUNT = "SELECT COUNT(*) FROM `bot_report` WHERE `reported_objectId` = ?";
    private static final String SQL_QUERY_SELECT_UNREAD = "SELECT `report_id`, `reported_name`, `reporter_name`, `date` FROM `bot_report` WHERE `read` = ?";
    private static final String SQL_QUERY_UPDATE_UNREAD = "UPDATE `bot_report` SET `read` = ? WHERE `report_id` = ?";
    private static final String SQL_QUERY_INSERT_REPORT = "INSERT INTO `bot_report`(`reported_name`, `reported_objectId`, `reporter_name`, `reporter_objectId`, `date`) VALUES (?,?,?,?,?)";

    public static BotReportDAO getInstance() {
        return instance;
    }

    public void deletePunish(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_DELETE_PUNISH);
            statement.setInt(1, player.getObjectId());
            statement.execute();
        } catch (Exception e) {
            logger.error("BotReportDAO: deletePunish() " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void restorePunish(final Player player) {
        String punish = "";
        long delay = 0;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_SELECT_PUNISH);
            statement.setInt(1, player.getObjectId());

            rset = statement.executeQuery();
            while (rset.next()) {
                punish = rset.getString("punish_type");
                delay = rset.getLong("time_left");
            }
        } catch (Exception e) {
            logger.error("BotReportDAO: restorePunish() " + e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        if (!punish.isEmpty() && BotPunish.Punish.valueOf(punish) != null) {
            if (delay < 0) {
                final BotPunish.Punish p = BotPunish.Punish.valueOf(punish);
                final long left = (-delay / 1000) / 60;
                player.getBotPunishComponent().setPunishDueBotting(p, (int) left);
            } else
                player.getBotPunishComponent().endPunishment();
        }
    }

    public void savePunish(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_UPDATE_PUNISH);
            statement.setLong(1, player.getBotPunishComponent().getPlayerPunish().getPunishTimeLeft());
            statement.setInt(2, player.getObjectId());
            statement.execute();
        } catch (Exception e) {
            logger.error("BotReportDAO: savePunish() " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public int requestPlayerReportsCount(final Player player) {
        int count = 0;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_SELECT_PUNISH_COUNT);
            statement.setInt(1, player.getObjectId());

            rset = statement.executeQuery();
            if (rset.next())
                count = rset.getInt(1);
        } catch (Exception e) {
            logger.error("BotReportDAO: requestPlayerReportsCount() " + e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return count;
    }

    public void saveUnread(final int markId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_UPDATE_UNREAD);
            statement.setString(1, "true");
            statement.setInt(2, markId);
            statement.execute();

            BotReportManager.getInstance().getUnread().remove(markId);
            logger.info("BotReportDAO: Reported bot marked as read, id was: " + markId);
        } catch (Exception e) {
            logger.warn("BotReportDAO: Could not mark as read the reported bot: " + markId + ":\n" + e.getMessage());
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void loadUnread() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_SELECT_UNREAD);
            statement.setString(1, "false");

            rset = statement.executeQuery();
            while (rset.next()) {
                final String[] data = new String[3];
                data[0] = rset.getString("reported_name");
                data[1] = rset.getString("reporter_name");
                data[2] = rset.getString("date");

                BotReportManager.getInstance().getUnread().put(rset.getInt("report_id"), data);
            }
        } catch (Exception e) {
            logger.warn("BotReportDAO: Could not load data from bot_report:\n" + e.getMessage());
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void saveReport(final Player playerReported, final Player playerReporter) {
        final long date = Calendar.getInstance().getTimeInMillis();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SQL_QUERY_INSERT_REPORT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, playerReported.getName());
            statement.setInt(2, playerReported.getObjectId());
            statement.setString(3, playerReporter.getName());
            statement.setInt(4, playerReporter.getObjectId());
            statement.setLong(5, date);
            statement.executeUpdate();

            rset = statement.getGeneratedKeys();
            rset.next();
            final int maxId = rset.getInt(1);

            BotReportManager.getInstance().getUnread().put(maxId, new String[]{playerReported.getName(), playerReporter.getName(), String.valueOf(date)});
        } catch (Exception e) {
            logger.warn("BotReportDAO: Could not save reported bot " + playerReported.getName() + " by " + playerReporter.getName() + " at " + date + '.');
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void introduceNewPunishedBotAndClear(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement delStatement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            // Introduce new Punished Bot in database
            statement = con.prepareStatement("INSERT INTO bot_reported_punish VALUES ( ?, ?, ? )");
            statement.setInt(1, player.getObjectId());
            statement.setString(2, player.getBotPunishComponent().getPlayerPunish().getBotPunishType().name());
            statement.setLong(3, player.getBotPunishComponent().getPlayerPunish().getPunishTimeLeft());
            statement.execute();

            // Delete all his reports from database
            delStatement = con.prepareStatement("DELETE FROM bot_report WHERE reported_objectId = ?");
            delStatement.setInt(1, player.getObjectId());
            delStatement.execute();
            DbUtils.closeQuietly(delStatement);
        } catch (Exception e) {
            logger.info("BotReportDAO: introduceNewPunishedBotAndClear(): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}