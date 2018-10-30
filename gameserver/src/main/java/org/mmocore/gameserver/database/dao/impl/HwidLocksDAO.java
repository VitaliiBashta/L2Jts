package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.jdbchelper.NoResultException;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;

/**
 * Created by Hack
 * Date: 13.09.2016 0:14
 */
public class HwidLocksDAO extends AbstractGameServerDAO {
    private static final HwidLocksDAO instance = new HwidLocksDAO();
    private static final String QUERY_SELECT_HWID = "SELECT hwid FROM hwid_locks WHERE account_name=? LIMIT 1";
    private static final String QUERY_REPLACE_HWID = "REPLACE INTO hwid_locks VALUES (?,?)";
    private static final String QUERY_DELETE_HWID = "DELETE FROM hwid_locks WHERE account_name=?";

    public static HwidLocksDAO getInstance() {
        return instance;
    }

    public String getHwid(String account) {
        try {
            return jdbcHelper.queryForString(QUERY_SELECT_HWID, account);
        } catch (NoResultException e) {
            return null;
        }
    }

    public void setHwid(String account, String hwid) {
        jdbcHelper.execute(QUERY_REPLACE_HWID, account, hwid);
    }

    public void removeHwid(String account) {
        jdbcHelper.execute(QUERY_DELETE_HWID, account);
    }
}
