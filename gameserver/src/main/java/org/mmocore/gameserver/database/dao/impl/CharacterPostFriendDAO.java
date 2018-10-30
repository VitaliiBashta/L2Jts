package org.mmocore.gameserver.database.dao.impl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 21:53/22.03.2011
 */
public class CharacterPostFriendDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterPostFriendDAO.class);
    private static final CharacterPostFriendDAO _instance = new CharacterPostFriendDAO();

    private static final String SELECT_SQL_QUERY = "SELECT pf.post_friend, c.char_name FROM character_post_friends pf LEFT JOIN characters c ON pf.post_friend = c.obj_Id WHERE pf.object_id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO character_post_friends(object_id, post_friend) VALUES (?,?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM character_post_friends WHERE object_id=? AND post_friend=?";

    public static CharacterPostFriendDAO getInstance() {
        return _instance;
    }

    public TIntObjectMap<String> select(final Player player) {
        final TIntObjectMap<String> set = new TIntObjectHashMap<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                final int objId = rset.getInt(1);
                final String name = rset.getString(2);
                if (objId > 0 && name != null && !name.isEmpty()) {
                    set.put(objId, name);
                }
            }
        } catch (Exception e) {
            _log.error("CharacterPostFriendDAO.select(L2Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return set;
    }

    public void insert(final Player player, final int val) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, val);
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterPostFriendDAO.insert(L2Player, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Player player, final int val) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, val);
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterPostFriendDAO.delete(L2Player, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
