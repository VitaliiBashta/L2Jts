package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Java-man
 */
public class AutoBanDAO extends AbstractGameServerDAO {
    private static final String SELECT_END_BAN_DATE = "SELECT MAX(endBanDate) AS endBanDate FROM bans WHERE obj_Id=?";
    private static final String INSERT_PLAYER_BAN = "INSERT INTO bans (account_name, obj_id, startBanDate, endBanDate, reason, gmName) VALUES(?,?,?,?,?,?)";
    private static final String INSERT_OFFLINE_PLAYER_BAN = "INSERT INTO bans (obj_id, startBanDate, endBanDate, reason, gmName) VALUES(?,?,?,?,?)";
    private static final String UPDATE_BAN_CHAT = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";
    private static final String DELETE_BAN = "DELETE FROM bans WHERE obj_id=?";
    private static final AutoBanDAO INSTANCE = new AutoBanDAO();

    private AutoBanDAO() {
    }

    public static AutoBanDAO getInstance() {
        return INSTANCE;
    }

    public long selectEndBanDate(final int objectId) {
        final long[] result = new long[1];

        jdbcHelper.query(SELECT_END_BAN_DATE, new ResultSetHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result[0] = rs.getLong("endBanDate");
            }
        }, stmt -> stmt.setInt(1, objectId));
        return result[0];
    }

    public void banPlayer(final Player actor, final long startBanDate, final long endBanDate, final String reason, final String gmName) {
        jdbcHelper.execute(INSERT_PLAYER_BAN, stmt -> {
            stmt.setString(1, actor.getAccountName());
            stmt.setInt(2, actor.getObjectId());
            stmt.setLong(3, startBanDate);
            stmt.setLong(4, endBanDate);
            stmt.setString(5, reason);
            stmt.setString(6, gmName);
        });
    }

    public void banOfflinePlayer(final int playerObjectId, final long startBanDate, final long endBanDate, final String reason, final String gmName) {
        jdbcHelper.execute(INSERT_OFFLINE_PLAYER_BAN, stmt -> {
            stmt.setInt(1, playerObjectId);
            stmt.setLong(2, startBanDate);
            stmt.setLong(3, endBanDate);
            stmt.setString(4, reason);
            stmt.setString(5, gmName);
        });
    }

    public void removeBan(final int playerObjectId) {
        jdbcHelper.execute(DELETE_BAN, stmt -> stmt.setInt(1, playerObjectId));
    }

    public void banChat(final int playerObjectId, final long time) {
        jdbcHelper.execute(UPDATE_BAN_CHAT, stmt -> {
            stmt.setLong(1, time > 0 ? time / 1000 : time);
            stmt.setInt(2, playerObjectId);
        });
    }
}
