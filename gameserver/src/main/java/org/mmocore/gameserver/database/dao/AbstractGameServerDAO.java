package org.mmocore.gameserver.database.dao;

import org.mmocore.commons.jdbchelper.JdbcHelper;
import org.mmocore.gameserver.database.DatabaseFactory;

/**
 * @author Java-man
 */
public abstract class AbstractGameServerDAO {
    protected final JdbcHelper jdbcHelper = new JdbcHelper(DatabaseFactory.getInstance().getConnectionPool());
}
