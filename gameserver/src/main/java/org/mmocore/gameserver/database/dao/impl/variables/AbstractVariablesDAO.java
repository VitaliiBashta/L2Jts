package org.mmocore.gameserver.database.dao.impl.variables;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.components.variables.Variables;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Java-man
 */
public abstract class AbstractVariablesDAO extends AbstractGameServerDAO {
    abstract String getSelectQuery();

    abstract String getSaveQuery();

    abstract String getRemoveQuery();

    abstract Variables getVariable(final String name);

    public MultiValueSet<Variables> loadVariables(final int objectId) {
        final MultiValueSet<Variables> variables = new MultiValueSet<>();
        jdbcHelper.query(getSelectQuery(), new ResultSetHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                final String name = rs.getString("name");
                final String value = rs.getString("value");

                variables.put(getVariable(name), value);
            }
        }, objectId);
        return variables;
    }

    public void save(final int objectId, final String variableName, final String value, final long expirationTime) {
        jdbcHelper.execute(getSaveQuery(), objectId, variableName, value, expirationTime);
    }

    public void remove(final int objectId, final String variableName) {
        jdbcHelper.execute(getRemoveQuery(), objectId, variableName);
    }
}
