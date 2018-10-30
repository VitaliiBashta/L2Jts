package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 18:10/15.04.2011
 */
public class DominionDAO {
    public static final String SELECT_SQL_QUERY = "SELECT lord_object_id, wards FROM dominion WHERE id=?";
    public static final String UPDATE_SQL_QUERY = "UPDATE dominion SET lord_object_id=?, wards=? WHERE id=?";
    private static final Logger _log = LoggerFactory.getLogger(DominionDAO.class);
    private static final DominionDAO _instance = new DominionDAO();

    public static DominionDAO getInstance() {
        return _instance;
    }

    public void select(final Dominion dominion) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, dominion.getId());
            rset = statement.executeQuery();
            if (rset.next()) {
                dominion.setLordObjectId(rset.getInt("lord_object_id"));

                final String flags = rset.getString("wards");
                if (!flags.isEmpty()) {
                    final String[] values = flags.split(";");
                    for (int i = 0; i < values.length; i++) {
                        dominion.addFlag(Integer.parseInt(values[i]));
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Dominion.loadData(): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void update(final Dominion residence) {
        if (!residence.getJdbcState().isUpdatable()) {
            return;
        }

        residence.setJdbcState(JdbcEntityState.STORED);
        update0(residence);
    }

    private void update0(final Dominion dominion) {
        final StringBuilder wardsString = new StringBuilder();
        final Integer[] flags = dominion.getFlags();
        if (flags.length > 0) {
            for (final int flag : flags) {
                wardsString.append(flag).append(';');
            }
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setInt(1, dominion.getLordObjectId());
            statement.setString(2, wardsString.toString());
            statement.setInt(3, dominion.getId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("DominionDAO#update0(Dominion): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
