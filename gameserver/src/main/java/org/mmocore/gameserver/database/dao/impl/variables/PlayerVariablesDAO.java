package org.mmocore.gameserver.database.dao.impl.variables;

import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.object.components.variables.Variables;

/**
 * @author Java-man
 */
public class PlayerVariablesDAO extends AbstractVariablesDAO {
    private static final String SELECT = "SELECT * FROM character_variables WHERE obj_id = ? AND `type`='player-var'";
    private static final String SAVE = "REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'player-var',?,?,?)";
    private static final String REMOVE = "DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='player-var' AND `name`=? LIMIT 1";
    private static final String SELECT_SQL_QUERY = "SELECT value FROM character_variables WHERE obj_id = ? AND `type`='player-var' AND name= ? LIMIT 1";

    private PlayerVariablesDAO() {
    }

    public static PlayerVariablesDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    String getSelectQuery() {
        return SELECT;
    }

    @Override
    String getSaveQuery() {
        return SAVE;
    }

    @Override
    String getRemoveQuery() {
        return REMOVE;
    }

    @Override
    Variables getVariable(final String name) {
        return PlayerVariables.valueOf(name);
    }

    public String getValue(final int objId, final String name) {
        return jdbcHelper.queryForString(SELECT_SQL_QUERY, objId, name);
    }

    private static class LazyHolder {
        private static final PlayerVariablesDAO INSTANCE = new PlayerVariablesDAO();
    }
}
