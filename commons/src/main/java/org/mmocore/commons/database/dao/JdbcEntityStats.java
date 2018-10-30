package org.mmocore.commons.database.dao;

public interface JdbcEntityStats {
    long getLoadCount();

    long getInsertCount();

    long getUpdateCount();

    long getDeleteCount();
}
