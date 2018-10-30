package org.mmocore.authserver.database.dao.impl;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.database.dao.AbstractAuthServerDAO;
import org.mmocore.commons.jdbchelper.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountsDAO extends AbstractAuthServerDAO {
    private static final AccountsDAO INSTANCE = new AccountsDAO();
    private static final String SQL_QUERY_SELECT_POINT = "SELECT points FROM accounts WHERE login=? LIMIT 1";
    private static final String SQL_QUERY_UPDATE_POINT = "UPDATE accounts SET points = ? WHERE login = ? LIMIT 1";
    private static final String SQL_QUERY_SELECT_REPORT_POINT = "SELECT report_points FROM accounts WHERE login=? LIMIT 1";
    private static final String SQL_QUERY_UPDATE_REPORT_POINT = "UPDATE accounts SET report_points = ? WHERE login = ? LIMIT 1";
    private static final String SQL_QUERY_RESET_REPORT_POINT = "UPDATE accounts SET report_points = ?";
    private static final String SQL_QUERY_SELECT = "SELECT id, password, access_level, ban_expire, allow_ip, last_server, last_ip, last_access, checkemail, l2email, report_points FROM accounts WHERE login = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO accounts (login, password) VALUES(?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE accounts SET password = ?, access_level = ?, ban_expire = ?, allow_ip = ?, last_server = ?, last_ip = ?, last_access = ?, checkemail = ?, l2email = ?, report_points = ? WHERE login = ?";

    public static AccountsDAO getInstance() {
        return INSTANCE;
    }

    public long requestGamePoints(final String login) {
        return jdbcHelper.queryForLong(SQL_QUERY_SELECT_POINT, login);
    }

    public void updateGamePoints(final String login, final long points) {
        jdbcHelper.execute(SQL_QUERY_UPDATE_POINT, points, login);
    }

    public int requestReportPoints(final String login) {
        return jdbcHelper.queryForInt(SQL_QUERY_SELECT_REPORT_POINT, login);
    }

    public void updateReportPoints(final String login, final int points) {
        jdbcHelper.execute(SQL_QUERY_UPDATE_REPORT_POINT, points, login);
    }

    public void resetReportPoints(final int reportPoints) {
        jdbcHelper.execute(SQL_QUERY_RESET_REPORT_POINT, reportPoints);
    }

    public void restore(final Account account) {
        jdbcHelper.query(SQL_QUERY_SELECT, new ResultSetHandler(1) {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                int i = 0;
                account.setAccountId(rs.getInt(++i));
                account.setPasswordHash(rs.getString(++i));
                account.setAccessLevel(rs.getInt(++i));
                account.setBanExpire(rs.getInt(++i));
                account.setAllowedIP(rs.getString(++i));
                account.setLastServer(rs.getInt(++i));
                account.setLastIP(rs.getString(++i));
                account.setLastAccess(rs.getInt(++i));
                account.setCheckEmail(rs.getInt(++i));
                account.setEmail(rs.getString(++i));
                account.setReportPoints(rs.getInt(++i));
            }
        }, account.getLogin());
        account.setPremium(AccountBonusDAO.getInstance().select(account.getLogin()));
    }

    public void save(final Account account) {
        jdbcHelper.execute(SQL_QUERY_INSERT, account.getLogin(), account.getPasswordHash());
    }

    public void update(final Account account) {
        jdbcHelper.execute(SQL_QUERY_UPDATE, account.getPasswordHash(), account.getAccessLevel(), account.getBanExpire(), account.getAllowedIP(), account.getLastServer(), account.getLastIP(), account.getLastAccess(), account.getCheckEmail(), account.getEmail(), account.getReportPoints(), account.getLogin());
    }
}