package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author VISTALL
 * @date 18:10/15.04.2011
 */
public class CastleDAO {
    public static final String SELECT_SQL_QUERY = "SELECT tax_percent, treasury, siege_date, last_siege_date, own_date FROM castle WHERE id=? LIMIT 1";
    public static final String UPDATE_SQL_QUERY = "UPDATE castle SET tax_percent=?, treasury=?, siege_date=?, last_siege_date=?, own_date=? WHERE id=?";
    private static final Logger _log = LoggerFactory.getLogger(CastleDAO.class);
    private static final CastleDAO _instance = new CastleDAO();

    public static CastleDAO getInstance() {
        return _instance;
    }

    public void select(final Castle castle) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, castle.getId());
            rset = statement.executeQuery();
            if (rset.next()) {
                castle.setTaxPercent(rset.getInt("tax_percent"));
                castle.setTreasury(rset.getLong("treasury"));

                final Instant siegeDate = Instant.ofEpochSecond(rset.getLong("siege_date"));
                castle.setSiegeDateOfInstant(siegeDate);

                final Instant lastSiegeDate = Instant.ofEpochSecond(rset.getLong("last_siege_date"));
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(lastSiegeDate, ZoneId.systemDefault());
                castle.setLastSiegeDate(zonedDateTime);

                final Instant ownDate = Instant.ofEpochSecond(rset.getLong("own_date"));
                zonedDateTime = ZonedDateTime.ofInstant(ownDate, ZoneId.systemDefault());
                castle.setOwnDate(zonedDateTime);
            }
        } catch (Exception e) {
            _log.error("CastleDAO.select(Castle):" + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void update(final Castle residence) {
        if (!residence.getJdbcState().isUpdatable()) {
            return;
        }

        residence.setJdbcState(JdbcEntityState.STORED);
        update0(residence);
    }

    private void update0(final Castle castle) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setInt(1, castle.getTaxPercent0());
            statement.setLong(2, castle.getTreasury());
            statement.setLong(3, castle.getSiegeDate().toEpochSecond());
            statement.setLong(4, castle.getLastSiegeDate().toEpochSecond());
            statement.setLong(5, castle.getOwnDate().toEpochSecond());
            statement.setInt(6, castle.getId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("CastleDAO#update0(Castle): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
