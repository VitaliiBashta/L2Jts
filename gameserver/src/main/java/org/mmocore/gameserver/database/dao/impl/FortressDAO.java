package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.residence.Fortress;
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
public class FortressDAO {
    public static final String SELECT_SQL_QUERY = "SELECT * FROM fortress WHERE id = ?";
    public static final String UPDATE_SQL_QUERY = "UPDATE fortress SET castle_id=?, state=?, cycle=?, reward_count=?, paid_cycle=?, supply_count=?, siege_date=?, last_siege_date=?, own_date=?, facility_0=?, facility_1=?, facility_2=?, facility_3=?, facility_4=? WHERE id=?";
    private static final Logger _log = LoggerFactory.getLogger(FortressDAO.class);
    private static final FortressDAO _instance = new FortressDAO();

    public static FortressDAO getInstance() {
        return _instance;
    }

    public void select(final Fortress fortress) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, fortress.getId());
            rset = statement.executeQuery();
            if (rset.next()) {
                fortress.setFortState(rset.getInt("state"), rset.getInt("castle_id"));
                fortress.setCycle(rset.getInt("cycle"));
                fortress.setRewardCount(rset.getInt("reward_count"));
                fortress.setPaidCycle(rset.getInt("paid_cycle"));
                fortress.setSupplyCount(rset.getInt("supply_count"));

                final Instant siegeDate = Instant.ofEpochSecond(rset.getLong("siege_date"));
                fortress.setSiegeDateOfInstant(siegeDate);

                final Instant lastSiegeDate = Instant.ofEpochSecond(rset.getLong("last_siege_date"));
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(lastSiegeDate, ZoneId.systemDefault());
                fortress.setLastSiegeDate(zonedDateTime);

                final Instant ownDate = Instant.ofEpochSecond(rset.getLong("own_date"));
                zonedDateTime = ZonedDateTime.ofInstant(ownDate, ZoneId.systemDefault());
                fortress.setOwnDate(zonedDateTime);

                for (int i = 0; i < Fortress.FACILITY_MAX; i++) {
                    fortress.setFacilityLevel(i, rset.getInt("facility_" + i));
                }
            }
        } catch (Exception e) {
            _log.error("FortressDAO.select(Fortress):" + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void update(final Fortress fortress) {
        if (!fortress.getJdbcState().isUpdatable()) {
            return;
        }

        fortress.setJdbcState(JdbcEntityState.STORED);
        update0(fortress);
    }

    private void update0(final Fortress fortress) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setInt(1, fortress.getCastleId());
            statement.setInt(2, fortress.getContractState());
            statement.setInt(3, fortress.getCycle());
            statement.setInt(4, fortress.getRewardCount());
            statement.setInt(5, fortress.getPaidCycle());
            statement.setInt(6, fortress.getSupplyCount());
            statement.setLong(7, fortress.getSiegeDate().toEpochSecond());
            statement.setLong(8, fortress.getLastSiegeDate().toEpochSecond());
            statement.setLong(9, fortress.getOwnDate().toEpochSecond());
            for (int i = 0; i < Fortress.FACILITY_MAX; i++) {
                statement.setInt(10 + i, fortress.getFacilityLevel(i));
            }
            statement.setInt(10 + Fortress.FACILITY_MAX, fortress.getId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("FortressDAO#update0(Fortress): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
