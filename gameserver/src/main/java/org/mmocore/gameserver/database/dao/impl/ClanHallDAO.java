package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
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
 * @date 19:19/15.04.2011
 */
public class ClanHallDAO {
    public static final String SELECT_SQL_QUERY = "SELECT siege_date, own_date, last_siege_date, auction_desc, auction_length, auction_min_bid, cycle, paid_cycle FROM clanhall WHERE id = ?";
    public static final String UPDATE_SQL_QUERY = "UPDATE clanhall SET siege_date=?, last_siege_date=?, own_date=?, auction_desc=?, auction_length=?, auction_min_bid=?, cycle=?, paid_cycle=? WHERE id=?";
    private static final Logger _log = LoggerFactory.getLogger(ClanHallDAO.class);
    private static final ClanHallDAO _instance = new ClanHallDAO();

    public static ClanHallDAO getInstance() {
        return _instance;
    }

    public void select(final ClanHall clanHall) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, clanHall.getId());
            rset = statement.executeQuery();
            if (rset.next()) {
                final Instant siegeDate = Instant.ofEpochSecond(rset.getLong("siege_date"));
                clanHall.setSiegeDateOfInstant(siegeDate);

                final Instant lastSiegeDate = Instant.ofEpochSecond(rset.getLong("last_siege_date"));
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(lastSiegeDate, ZoneId.systemDefault());
                clanHall.setLastSiegeDate(zonedDateTime);

                final Instant ownDate = Instant.ofEpochSecond(rset.getLong("own_date"));
                zonedDateTime = ZonedDateTime.ofInstant(ownDate, ZoneId.systemDefault());
                clanHall.setOwnDate(zonedDateTime);

                //
                clanHall.setAuctionLength(rset.getInt("auction_length"));
                clanHall.setAuctionMinBid(rset.getLong("auction_min_bid"));
                clanHall.setAuctionDescription(rset.getString("auction_desc"));
                //
                clanHall.setCycle(rset.getInt("cycle"));
                clanHall.setPaidCycle(rset.getInt("paid_cycle"));
            }
        } catch (Exception e) {
            _log.error("ClanHallDAO.select(ClanHall):" + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void update(final ClanHall c) {
        if (!c.getJdbcState().isUpdatable()) {
            return;
        }

        c.setJdbcState(JdbcEntityState.STORED);
        update0(c);
    }

    private void update0(final ClanHall c) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setLong(1, c.getSiegeDate().toEpochSecond());
            statement.setLong(2, c.getLastSiegeDate().toEpochSecond());
            statement.setLong(3, c.getOwnDate().toEpochSecond());
            statement.setString(4, c.getAuctionDescription());
            statement.setInt(5, c.getAuctionLength());
            statement.setLong(6, c.getAuctionMinBid());
            statement.setInt(7, c.getCycle());
            statement.setInt(8, c.getPaidCycle());
            statement.setInt(9, c.getId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("ClanHallDAO#update0(ClanHall): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
