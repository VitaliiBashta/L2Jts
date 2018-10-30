package org.mmocore.gameserver.database.dao.impl.variables;

import org.mmocore.gameserver.object.components.variables.AccountVariables;
import org.mmocore.gameserver.object.components.variables.Variables;

/**
 * @author Java-man
 */
public class AccountVariablesDAO extends AbstractVariablesDAO {
    private static final String SELECT = "SELECT * FROM character_variables WHERE obj_id = ? AND `type`='account-var'";
    private static final String SAVE = "REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'account-var',?,?,?)";
    private static final String REMOVE = "DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='account-var' AND `name`=? LIMIT 1";

    private AccountVariablesDAO() {
    }

    public static AccountVariablesDAO getInstance() {
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
        return AccountVariables.valueOf(name);
    }

    private static class LazyHolder {
        private static final AccountVariablesDAO INSTANCE = new AccountVariablesDAO();
    }
}
