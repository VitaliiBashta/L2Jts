package org.jts.protection.database;

import org.jts.protection.manager.HWIDBanManager.BanType;
import org.jts.protection.manager.hwid.HWIDInfo;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;

/**
 * @author KilRoy
 */
public class ProtectionDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtectionDAO.class);

    private static final String SELECT_SQL_QUERY = "SELECT * FROM hwid_list";
    private static final String SELECT_BAN_SQL_QUERY = "SELECT * FROM hwid_bans";
    private static final String UPDATE_SQL_QUERY = "UPDATE hwid_list SET login=? WHERE hwid=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO hwid_list (hwid, login) values (?,?)";
    private static final String INSERT_BAN_SQL_QUERY = "INSERT INTO hwid_bans (hwid, comment, ban_type) values (?,?,?)";
    private static final String INSERT_INFO_SQL_QUERY = "INSERT INTO protection_info_list (login, buf) values (?,?)";
    private static final String SELECT_INFO_SQL_QUERY = "SELECT * FROM protection_info_list";
    private static final String UPDATE_INFO_SQL_QUERY = "UPDATE protection_info_list SET login=? WHERE buf=?";

    private ProtectionDAO() {
    }

    public static ProtectionDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void closeQuietly(final Connection conn, final Statement stmt, final ResultSet rs) {
        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }
    }

    public static void closeQuietly(final ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            // quiet
        }
    }

    public static void closeQuietly(final Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            // quiet
        }
    }

    public static void closeQuietly(final Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            // quiet
        }
    }

    public HashMap<Integer, HWIDInfo> loadHWIDList() {
        final HashMap<Integer, HWIDInfo> listHWID = new HashMap<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            rset = statement.executeQuery();
            int counterHWIDInfo = 0;
            while (rset.next()) {
                final HWIDInfo hwidInfo = new HWIDInfo(counterHWIDInfo);
                hwidInfo.setHWID(rset.getString("hwid"));
                hwidInfo.setLogin(rset.getString("login"));
                listHWID.put(counterHWIDInfo, hwidInfo);
                counterHWIDInfo++;
            }
        } catch (final SQLException e) {
            LOGGER.error("Loading error loadHWIDList()", e);
        } finally {
            closeQuietly(con, statement, rset);
        }

        return listHWID;
    }

    public HashMap<Integer, HWIDInfo> loadHWIDListBanned() {
        final HashMap<Integer, HWIDInfo> listHWIDBanned = new HashMap<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        String HWID;
        int counterHWIDBan = 0;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_BAN_SQL_QUERY);
            rset = statement.executeQuery();
            while (rset.next()) {
                HWID = rset.getString("hwid");
                final HWIDInfo hwidBan = new HWIDInfo(counterHWIDBan);
                hwidBan.setHWIDBanned(HWID);
                hwidBan.setBanType(BanType.valueOf(rset.getString("ban_type")));
                listHWIDBanned.put(counterHWIDBan, hwidBan);
                counterHWIDBan++;
            }
        } catch (final SQLException e) {
            LOGGER.error("Loading error loadHWIDListBanned()", e);
        } finally {
            closeQuietly(con, statement, rset);
        }
        return listHWIDBanned;
    }

    public HashMap<String, String> loadProcBufInfo() {
        final HashMap<String, String> procBufInfo = new HashMap<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_INFO_SQL_QUERY);
            rset = statement.executeQuery();
            while (rset.next()) {
                final String login = rset.getString("login");
                final String buf = rset.getString("buf");
                procBufInfo.put(login, buf);
            }
        } catch (final SQLException e) {
            LOGGER.error("Loading error loadProcBufInfo()", e);
        } finally {
            closeQuietly(con, statement, rset);
        }
        return procBufInfo;
    }

    public void storeHWID(final GameClient client, final boolean founded) {
        Connection con = null;
        PreparedStatement statement = null;
        if (founded) {
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement(UPDATE_SQL_QUERY);
                statement.setString(1, client.getLogin());
                statement.setString(2, client.getHWID());
                statement.execute();
            } catch (final SQLException e) {
                LOGGER.error("Store error storeHWID(Class, boolean)(founded)", e);
            } finally {
                closeQuietly(con, statement, null);
            }
        } else {
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement(INSERT_SQL_QUERY);
                statement.setString(1, client.getHWID());
                statement.setString(2, client.getLogin());
                statement.execute();
            } catch (final Exception e) {
                LOGGER.error("Store error storeHWID(Class, boolean)", e);
            } finally {
                closeQuietly(con, statement, null);
            }
        }
    }

    public void storeHWIDBanned(final GameClient client, final String comment, final BanType banType) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_BAN_SQL_QUERY);
            statement.setString(1, client.getHWID());
            statement.setString(2, comment);
            statement.setString(3, banType.toString());
            statement.execute();
        } catch (final SQLException e) {
            LOGGER.error("Store error storeHWIDBanned(Class, String, Class)", e);
        } finally {
            closeQuietly(con, statement, null);
        }
    }

    public void storeProcBufInfo(final String login, final String buf) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_INFO_SQL_QUERY);
            statement.setString(1, login);
            statement.setString(2, buf);
            statement.execute();
        } catch (final SQLException e) {
            LOGGER.error("Store error storeProcBufInfo(String, String)", e);
        } finally {
            closeQuietly(con, statement, null);
        }
    }

    public void updateProcBufInfo(final String login, final String buf) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_INFO_SQL_QUERY);
            statement.setString(1, login);
            statement.setString(2, buf);
            statement.execute();
        } catch (final SQLException e) {
            LOGGER.error("Store error updateProcBufInfo(String, String)", e);
        } finally {
            closeQuietly(con, statement, null);
        }
    }

    private static class LazyHolder {
        private static final ProtectionDAO INSTANCE = new ProtectionDAO();
    }
}