package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMark;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMarkComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 20:07/08.08.2011
 */
public class CharacterTeleportBookmarkDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterTeleportBookmarkDAO.class);
    private static final CharacterTeleportBookmarkDAO _instance = new CharacterTeleportBookmarkDAO();

    private static final String SELECT_SQL_QUERY = "SELECT * FROM character_tp_bookmarks WHERE object_id=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO character_tp_bookmarks(object_id, name, acronym, icon, x, y, z) VALUES (?,?,?,?,?,?,?)";
    private static final String UPDATE_SQL_QUERY = "UPDATE character_tp_bookmarks SET name=?, acronym=?, icon=? WHERE object_id=? AND name=? AND x=? AND y=? AND z=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM character_tp_bookmarks WHERE object_id=? AND name=? AND x=? AND y=? AND z=? LIMIT 1";

    public static CharacterTeleportBookmarkDAO getInstance() {
        return _instance;
    }

    public List<TeleportBookMark> select(final Player player) {
        final List<TeleportBookMark> list = new ArrayList<>(TeleportBookMarkComponent.MAX_TELEPORT_BOOKMARK_SIZE);

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                final int x = rset.getInt("x");
                final int y = rset.getInt("y");
                final int z = rset.getInt("z");
                final int icon = rset.getInt("icon");
                final String name = rset.getString("name");
                final String acronym = rset.getString("acronym");

                list.add(new TeleportBookMark(x, y, z, icon, name, acronym));
            }
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.select(Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return list;
    }

    public void insert(final Player player, final TeleportBookMark bookMark) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.setString(2, bookMark.getName());
            statement.setString(3, bookMark.getAcronym());
            statement.setInt(4, bookMark.getIcon());
            statement.setInt(5, bookMark.getX());
            statement.setInt(6, bookMark.getY());
            statement.setInt(7, bookMark.getZ());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.insert(Player, TpBookMark): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Player player, final TeleportBookMark bookMark) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.setString(2, bookMark.getName());
            statement.setInt(3, bookMark.getX());
            statement.setInt(4, bookMark.getY());
            statement.setInt(5, bookMark.getZ());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.delete(Player,TpBookMark): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void update(final Player player, final TeleportBookMark bookMark) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setString(1, bookMark.getName());
            statement.setString(2, bookMark.getAcronym());
            statement.setInt(3, bookMark.getIcon());
            statement.setInt(4, player.getObjectId());
            statement.setString(5, bookMark.getName());
            statement.setInt(6, bookMark.getX());
            statement.setInt(7, bookMark.getY());
            statement.setInt(8, bookMark.getZ());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.update(Player,TpBookMark): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
