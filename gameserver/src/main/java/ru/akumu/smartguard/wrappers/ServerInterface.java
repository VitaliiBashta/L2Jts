package ru.akumu.smartguard.wrappers;

import ru.akumu.smartguard.core.wrappers.IServerInterface;
import ru.akumu.smartguard.core.wrappers.IWorld;
import ru.akumu.smartguard.core.wrappers.db.IConnectionFactory;
import ru.akumu.smartguard.wrappers.db.ConnectionFactory;

/**
 * @author Akumu
 * @date 27.03.2016 11:58
 */
public class ServerInterface extends IServerInterface {
    ConnectionFactory factory;
    World world;

    public ServerInterface() {
        protocol = 4;
        factory = new ConnectionFactory();
        world = new World();
    }

    @Override
    public IConnectionFactory getConnectionFactory() {
        return factory;
    }

    @Override
    public IWorld getWorld() {
        return world;
    }

    @Override
    public boolean onEvent(Event e, final Object... args) {
        return true;
    }
}
