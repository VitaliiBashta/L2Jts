package org.mmocore.authserver.database.dao;

import org.mmocore.authserver.database.DatabaseFactory;
import org.mmocore.commons.jdbchelper.JdbcHelper;


public abstract class AbstractAuthServerDAO {
    protected final JdbcHelper jdbcHelper = new JdbcHelper(DatabaseFactory.getInstance().getConnectionPool());
}
