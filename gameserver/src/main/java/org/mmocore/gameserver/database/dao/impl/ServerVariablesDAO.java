package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Java-man
 */
public class ServerVariablesDAO extends AbstractGameServerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerVariablesDAO.class);

    private static final ServerVariablesDAO INSTANCE = new ServerVariablesDAO();

    private static final String SELECT_ALL = "SELECT * FROM server_variables";
    private static final String DELETE = "DELETE FROM server_variables WHERE name = ?";
    private static final String REPLACE = "REPLACE INTO server_variables (name, value) VALUES (?,?)";

    private ServerVariablesDAO() {
    }

    public static ServerVariablesDAO getInstance() {
        return INSTANCE;
    }

    public StatsSet selectAll() {
        final StatsSet result = new StatsSet();
        jdbcHelper.query(SELECT_ALL, new ResultSetHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.set(rs.getString("name"), rs.getString("value"));
            }
        });
        return result;
    }

    public void save(final String name, final String value) {
        if (value.isEmpty()) {
            jdbcHelper.execute(DELETE, name);
        } else {
            jdbcHelper.execute(REPLACE, name, value);
        }
    }
}
