package org.mmocore.gameserver.object.components.variables;

import org.mmocore.gameserver.database.dao.impl.variables.AccountVariablesDAO;
import org.mmocore.gameserver.network.lineage.GameClient;

/**
 * @author Java-man
 */
public class AccountVariablesComponent extends AbstractVariablesComponent<AccountVariables> {
    public AccountVariablesComponent(final GameClient gameClient, boolean isCanUseDao) {
        super(gameClient.getAccountId(), AccountVariablesDAO.getInstance(), isCanUseDao);
    }

    public AccountVariablesComponent(final GameClient gameClient) {
        this(gameClient, true);
    }
}
