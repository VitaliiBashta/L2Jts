package org.mmocore.authserver.manager;

import org.mmocore.authserver.configuration.config.LoginConfig;
import org.mmocore.authserver.database.dao.impl.GameServerDAO;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.commons.net.utils.NetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameServerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerManager.class);
    private static final GameServerManager INSTANCE = new GameServerManager();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private Set<GameServer> gameServers = new LinkedHashSet<>();
    public GameServerManager() {
        load();
        LOGGER.info("Loaded " + gameServers.size() + " registered GameServer(s).");
    }

    public static GameServerManager getInstance() {
        return INSTANCE;
    }

    private void load() {
        gameServers = GameServerDAO.getInstance().load();
    }

    /**
     * Поулчить массив всех зарегистрированных игровых серверов
     *
     * @return массив всех игровых серверов
     */
    public GameServer[] getGameServers() {
        readLock.lock();
        try {
            return gameServers.toArray(new GameServer[gameServers.size()]);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Получить зарегистрированный игровой сервер по идентификатору
     *
     * @param id идентификатор игрового сервера
     * @return игровой сервер
     */
    public GameServer getGameServerById(int id) {
        readLock.lock();
        try {
            for (final GameServer gameServer : gameServers) {
                GameServer.Entry entry = gameServer.entries.get(id);
                if (entry != null) {
                    return gameServer;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Регистрация игрового сервера на любой свободный идентификатор
     *
     * @param gs игровой сервер
     * @return true если игрвоой сервер успешно зарегистрирован
     */
    public GameServer.Entry registerGameServer(GameServer gs, List<NetInfo> list) {
        if (!LoginConfig.ACCEPT_NEW_GAMESERVER)
            return null;

        writeLock.lock();
        try {
            int id = 1;
            while (id++ > 0) {
                GameServer gameServerById = getGameServerById(id);
                if (gameServerById == null || !gameServerById.isAuthed()) {
                    for (GameServer gameServer : gameServers) {
                        gameServer.entries.remove(id);
                    }

                    gameServers.add(gs);

                    GameServer.Entry value = new GameServer.Entry(id);
                    value.infos = list;
                    gs.entries.put(id, value);
                    return value;
                }
            }
        } finally {
            writeLock.unlock();
        }
        return null;
    }

    /**
     * Регистрация игрового сервера на требуемый идентификатор
     *
     * @param id требуемый идентификатор игрового сервера
     * @param gs игровой сервер
     * @return true если игрвоой сервер успешно зарегистрирован
     */
    public GameServer.Entry registerGameServer(int id, GameServer gs, List<NetInfo> list) {
        writeLock.lock();
        try {
            GameServer pgs = getGameServerById(id);
            if (!LoginConfig.ACCEPT_NEW_GAMESERVER && pgs == null) {
                return null;
            }
            if (pgs != null && pgs.isAuthed()) {
                return null;
            }

            for (final GameServer gameServer : gameServers) {
                gameServer.entries.remove(id);
            }
            gameServers.add(gs);
            GameServer.Entry value = new GameServer.Entry(id);
            value.infos = list;
            gs.entries.put(id, value);
            return value;
        } finally {
            writeLock.unlock();
        }
    }
}