package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 14:36/19.09.2011
 */
public class CharacterServitorDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterServitorDAO.class);

    private static final CharacterServitorDAO _instance = new CharacterServitorDAO();

    private static final String SELECT_SQL_QUERY = "SELECT type, servitor_id FROM character_servitors WHERE object_id=?";
    private static final String INSERT_SQL_QUERY = "REPLACE INTO character_servitors(object_id, servitor_id, type) VALUES (?,?,?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM character_servitors WHERE object_id=?";
    private static final String DELETE_SQL_QUERY2 = "DELETE FROM character_servitors WHERE object_id = ? AND servitor_id=? AND type=?";

    private CharacterServitorDAO() {
    }

    public static CharacterServitorDAO getInstance() {
        return _instance;
    }

    public List<int[]> select(final Player player) {
        List<int[]> list = Collections.emptyList();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                if (list.isEmpty()) {
                    list = new ArrayList<>(1);
                }

                list.add(new int[]{rset.getInt(1), rset.getInt(2)});
            }
        } catch (Exception e) {
            _log.error("CharacterServitorDAO.select(Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return list;
    }

    public void delete(final int objectId) {
        Connection con = null;
        PreparedStatement statement = null;
        final ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, objectId);
            statement.execute();
            DbUtils.close(statement);
        } catch (Exception e) {
            _log.error("CharacterServitorDAO.delete(int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void delete(final int objectId, final int servitorId, final int type) {
        Connection con = null;
        PreparedStatement statement = null;
        final ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY2);
            statement.setInt(1, objectId);
            statement.setInt(2, servitorId);
            statement.setInt(3, type);
            statement.execute();
            DbUtils.close(statement);
        } catch (Exception e) {
            _log.error("CharacterServitorDAO.delete(int, int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void insert(final Player player, final Servitor summon) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, summon.isSummon() ? ((SummonInstance) summon).getCallSkillId() : summon.getControlItemObjId());
            statement.setInt(3, summon.getServitorType());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterServitorDAO.insert(Player, Summon): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}