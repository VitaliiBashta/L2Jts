package org.mmocore.gameserver.database;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public abstract class mysql {
    private static final Logger _log = LoggerFactory.getLogger(mysql.class);

    /**
     * Выполняет простой sql запросов, где ненужен контроль параметров<BR>
     * ВНИМАНИЕ: В данном методе передаваемые параметры не проходят проверку на предмет SQL-инъекции!
     *
     * @param query Строка SQL запроса
     * @return false в случае ошибки выполнения запроса либо true в случае успеха
     */
    public static boolean setEx(DatabaseFactory db, final String query, final Object... vars) {
        Connection con = null;
        Statement statement = null;
        PreparedStatement pstatement = null;
        try {
            if (db == null) {
                db = DatabaseFactory.getInstance();
            }
            con = db.getConnection();
            if (vars.length == 0) {
                statement = con.createStatement();
                statement.executeUpdate(query);
            } else {
                pstatement = con.prepareStatement(query);
                setVars(pstatement, vars);
                pstatement.executeUpdate();
            }
        } catch (Exception e) {
            _log.warn("Could not execute update '{}': {}", query, e);
            e.printStackTrace();
            return false;
        } finally {
            DbUtils.closeQuietly(con, vars.length == 0 ? statement : pstatement);
        }
        return true;
    }

    public static void setVars(final PreparedStatement statement, final Object... vars) throws SQLException {
        Number n;
        long long_val;
        double double_val;
        for (int i = 0; i < vars.length; i++) {
            if (vars[i] instanceof Number) {
                n = (Number) vars[i];
                long_val = n.longValue();
                double_val = n.doubleValue();
                if (long_val == double_val) {
                    statement.setLong(i + 1, long_val);
                } else {
                    statement.setDouble(i + 1, double_val);
                }
            } else if (vars[i] instanceof String) {
                statement.setString(i + 1, (String) vars[i]);
            }
        }
    }

    public static boolean set(final String query, final Object... vars) {
        return setEx(null, query, vars);
    }

    public static boolean set(final String query) {
        return setEx(null, query);
    }

    public static List<Object> get_array(DatabaseFactory db, final String query) {
        final List<Object> ret = new ArrayList<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            if (db == null) {
                db = DatabaseFactory.getInstance();
            }
            con = db.getConnection();
            statement = con.prepareStatement(query);
            rset = statement.executeQuery();
            final ResultSetMetaData md = rset.getMetaData();

            while (rset.next()) {
                if (md.getColumnCount() > 1) {
                    final Map<String, Object> tmp = new HashMap<>();
                    for (int i = 0; i < md.getColumnCount(); i++) {
                        tmp.put(md.getColumnName(i + 1), rset.getObject(i + 1));
                    }
                    ret.add(tmp);
                } else {
                    ret.add(rset.getObject(1));
                }
            }
        } catch (Exception e) {
            _log.warn("Could not execute query '" + query + "': " + e);
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return ret;
    }

    public static List<Object> get_array(final String query) {
        return get_array(null, query);
    }

    public static int simple_get_int(final String ret_field, final String table, final String where) {
        final String query = "SELECT " + ret_field + " FROM `" + table + "` WHERE " + where + " LIMIT 1;";

        int res = 0;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rset = statement.executeQuery();

            if (rset.next()) {
                res = rset.getInt(1);
            }
        } catch (Exception e) {
            _log.warn("mSGI: Error in query '" + query + "':" + e);
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return res;
    }
}