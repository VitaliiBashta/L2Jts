package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

/**
 * @author VISTALL
 * @date 12:11/25.06.2011
 */
public class DominionRewardDAO {
    private static final String INSERT_SQL_QUERY = "INSERT INTO dominion_rewards (id, object_id, static_badges, online_reward, kill_reward) VALUES";
    private static final String SELECT_SQL_QUERY = "SELECT * FROM dominion_rewards WHERE id=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM dominion_rewards WHERE id=? AND object_id=?";
    private static final String DELETE_SQL_QUERY2 = "DELETE FROM dominion_rewards WHERE id=?";

    private static final Logger _log = LoggerFactory.getLogger(DominionRewardDAO.class);
    private static final DominionRewardDAO _instance = new DominionRewardDAO();

    public static DominionRewardDAO getInstance() {
        return _instance;
    }

    public void select(final Dominion d) {
        final DominionSiegeEvent siegeEvent = d.getSiegeEvent();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, d.getId());
            rset = statement.executeQuery();
            while (rset.next()) {
                final int playerObjectId = rset.getInt("object_id");
                final int staticBadges = rset.getInt("static_badges");
                final int onlineReward = rset.getInt("online_reward");
                final int killReward = rset.getInt("kill_reward");

                siegeEvent.setReward(playerObjectId, DominionSiegeEvent.STATIC_BADGES, staticBadges);
                siegeEvent.setReward(playerObjectId, DominionSiegeEvent.KILL_REWARD, killReward);
                siegeEvent.setReward(playerObjectId, DominionSiegeEvent.ONLINE_REWARD, onlineReward);
            }
        } catch (Exception e) {
            _log.error("DominionRewardDAO:select(Dominion): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void insert(final Dominion d) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY2);
            statement.setInt(1, d.getId());
            statement.execute();

            final DominionSiegeEvent siegeEvent = d.getSiegeEvent();
            final Set<Map.Entry<Integer, int[]>> rewards = siegeEvent.getRewards();

            final SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
            for (final Map.Entry<Integer, int[]> entry : rewards) {
                final StringBuilder sb = new StringBuilder("(");
                sb.append(d.getId()).append(',');
                sb.append(entry.getKey()).append(',');
                sb.append(entry.getValue()[DominionSiegeEvent.STATIC_BADGES]).append(',');
                sb.append(entry.getValue()[DominionSiegeEvent.ONLINE_REWARD]).append(',');
                sb.append(entry.getValue()[DominionSiegeEvent.KILL_REWARD]).append(')');
                b.write(sb.toString());
            }

            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (final Exception e) {
            _log.error("DominionRewardDAO.insert(Dominion):", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Dominion d, final int objectId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, d.getId());
            statement.setInt(2, objectId);
            statement.execute();
        } catch (Exception e) {
            _log.error("DominionRewardDAO:delete(Dominion): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
