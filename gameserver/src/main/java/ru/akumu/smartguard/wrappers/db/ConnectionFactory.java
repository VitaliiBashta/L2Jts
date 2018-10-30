package ru.akumu.smartguard.wrappers.db;

import org.mmocore.gameserver.database.DatabaseFactory;
import ru.akumu.smartguard.core.wrappers.db.IConnection;
import ru.akumu.smartguard.core.wrappers.db.IConnectionFactory;

import java.sql.SQLException;

/**
 * @author Akumu
 * @date 27.03.2016 12:00
 */
public class ConnectionFactory extends IConnectionFactory {

    @Override
    public IConnection getConnection() {
        return new Connection(DatabaseFactory.getInstance().getConnection());
    }
}
