package org.mmocore.authserver.database;

import org.mmocore.commons.database.factory.AbstractDatabaseFactory;

public class DatabaseFactory extends AbstractDatabaseFactory {
    private static final DatabaseFactory INSTANCE = new DatabaseFactory();

    public static final DatabaseFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getConfigPath() {
        return "configuration/database/authdatabase.properties";
    }
}
