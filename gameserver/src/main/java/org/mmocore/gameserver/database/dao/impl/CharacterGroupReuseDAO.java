package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

import static java.util.Map.Entry;

/**
 * @author VISTALL
 * @date 11:41/28.03.2011
 */
public class CharacterGroupReuseDAO {
    public static final String DELETE_SQL_QUERY = "DELETE FROM character_group_reuse WHERE object_id=?";
    public static final String SELECT_SQL_QUERY = "SELECT * FROM character_group_reuse WHERE object_id=?";
    public static final String INSERT_SQL_QUERY = "REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES";
    private static final Logger _log = LoggerFactory.getLogger(CharacterGroupReuseDAO.class);
    private static final CharacterGroupReuseDAO _instance = new CharacterGroupReuseDAO();

    public static CharacterGroupReuseDAO getInstance() {
        return _instance;
    }

    public void select(final Player player) {
        final long curTime = System.currentTimeMillis();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                final int group = rset.getInt("reuse_group");
                final int item_id = rset.getInt("item_id");
                final long endTime = rset.getLong("end_time");
                final long reuse = rset.getLong("reuse");

                if (endTime - curTime > 500) {
                    final TimeStamp stamp = new TimeStamp(item_id, endTime, reuse);
                    player.addSharedGroupReuse(group, stamp);
                }
            }
            DbUtils.close(statement);

            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterGroupReuseDAO.select(L2Player):", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void insert(final Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.execute();

            final Set<Entry<Integer, TimeStamp>> reuses = player.getSharedGroupReuses();
            if (reuses.isEmpty()) {
                return;
            }

            final SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
            synchronized (reuses) {
                for (final Entry<Integer, TimeStamp> entry : reuses) {
                    final int group = entry.getKey();
                    final TimeStamp timeStamp = entry.getValue();
                    if (timeStamp.hasNotPassed()) {
                        final StringBuilder sb = new StringBuilder("(");
                        sb.append(player.getObjectId()).append(',');
                        sb.append(group).append(',');
                        sb.append(timeStamp.getId()).append(',');
                        sb.append(timeStamp.getEndTime()).append(',');
                        sb.append(timeStamp.getReuseBasic()).append(')');
                        b.write(sb.toString());
                    }
                }
            }
            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (final Exception e) {
            _log.error("CharacterGroupReuseDAO.insert(L2Player):", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
