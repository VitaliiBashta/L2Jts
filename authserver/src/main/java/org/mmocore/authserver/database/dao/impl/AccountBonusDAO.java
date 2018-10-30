package org.mmocore.authserver.database.dao.impl;

import org.mmocore.authserver.component.Premium;
import org.mmocore.authserver.database.DatabaseFactory;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountBonusDAO {
    public static final String SELECT_SQL_QUERY = "SELECT rate_xp, rate_sp, rate_adena, rate_drop, rate_spoil, rate_epaulette, bonus_expire FROM account_bonus WHERE account=?";
    public static final String DELETE_SQL_QUERY = "DELETE FROM account_bonus WHERE account=?";
    public static final String INSERT_SQL_QUERY = "REPLACE INTO account_bonus(account, rate_xp, rate_sp, rate_adena, rate_drop, rate_spoil, rate_epaulette, bonus_expire) VALUES (?,?,?,?,?,?,?,?)";
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountBonusDAO.class);
    private static final AccountBonusDAO _instance = new AccountBonusDAO();

    public static AccountBonusDAO getInstance() {
        return _instance;
    }

    public Premium select(final String account) {
        final Premium premium = new Premium();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setString(1, account);
            rset = statement.executeQuery();
            if (rset.next()) {
                premium.setRateXp(rset.getDouble("rate_xp"));
                premium.setRateSp(rset.getDouble("rate_sp"));
                premium.setDropAdena(rset.getDouble("rate_adena"));
                premium.setDropItems(rset.getDouble("rate_drop"));
                premium.setDropSpoil(rset.getDouble("rate_spoil"));
                premium.setBonusEpaulette(rset.getDouble("rate_epaulette"));
                premium.setBonusExpire(rset.getInt("bonus_expire"));
            }
        } catch (Exception e) {
            LOGGER.info("AccountBonusDAO.select(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return premium;
    }

    public void delete(final String account) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setString(1, account);
            statement.execute();
        } catch (Exception e) {
            LOGGER.info("AccountBonusDAO.delete(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void insert(final String account, final Premium premium) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, account);
            statement.setDouble(2, premium.getRateXp());
            statement.setDouble(3, premium.getRateSp());
            statement.setDouble(4, premium.getDropAdena());
            statement.setDouble(5, premium.getDropItems());
            statement.setDouble(6, premium.getDropSpoil());
            statement.setDouble(7, premium.getBonusEpaulette());
            statement.setLong(8, premium.getBonusExpire());
            statement.execute();
        } catch (Exception e) {
            LOGGER.info("AccountBonusDAO.insert(): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}