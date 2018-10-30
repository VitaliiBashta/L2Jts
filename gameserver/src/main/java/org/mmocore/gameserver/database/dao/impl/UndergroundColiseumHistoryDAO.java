package org.mmocore.gameserver.database.dao.impl;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 16:00/15.05.2012
 */
public class UndergroundColiseumHistoryDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UndergroundColiseumHistoryDAO.class);
    private static final String SELECT_SQL_QUERY = "SELECT * FROM underground_coliseum_history WHERE id=?";
    private static final String UPDATE_SQL_QUERY = "UPDATE underground_coliseum_history SET wins=? WHERE id=? AND name=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO underground_coliseum_history(id, name, wins) VALUES (?,?,?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM underground_coliseum_history WHERE id=?";
    private static UndergroundColiseumHistoryDAO INSTANCE = new UndergroundColiseumHistoryDAO();

    private UndergroundColiseumHistoryDAO() {
    }

    public static UndergroundColiseumHistoryDAO getInstance() {
        return INSTANCE;
    }

    public List<Pair<String, Integer>> select(int id) {
        final List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, id);
            rset = statement.executeQuery();
            while (rset.next())
                list.add(new MutablePair<String, Integer>(rset.getString("name"), rset.getInt("wins")));
        } catch (Exception e) {
            LOGGER.warn("UndergroundColiseumHistoryDAO:select(int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return list;
    }

    public void update(int id, Pair<String, Integer> pair) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setInt(1, pair.getValue());
            statement.setInt(2, id);
            statement.setString(3, pair.getKey());
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("UndergroundColiseumHistoryDAO:update(int, Pair<String, Integer>): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void insert(int id, Pair<String, Integer> pair) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, id);
            statement.setString(2, pair.getKey());
            statement.setInt(3, pair.getValue());
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("UndergroundColiseumHistoryDAO:insert(int, Pair<String, Integer>): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(int id) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            LOGGER.warn("UndergroundColiseumHistoryDAO:delete(int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}