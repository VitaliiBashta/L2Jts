package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.configuration.config.LoginConfig;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingResponse extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(PingResponse.class);

    private long _serverTime;

    @Override
    protected void readImpl() {
        _serverTime = readQ();
    }

    @Override
    protected void runImpl() {
        GameServer gameServer = getGameServer();
        if (!gameServer.isAuthed()) {
            return;
        }

        gameServer.getConnection().onPingResponse();

        long diff = System.currentTimeMillis() - _serverTime;

        if (Math.abs(diff) > LoginConfig.GAME_SERVER_PING_TIMEOUT_ALERT) {
            for (GameServer.Entry entry : gameServer.entries.values()) {
                _log.warn("Gameserver " + entry.getId() + " [" + entry.getName() + "] : time offset " + diff + " ms.");
            }
        }
    }
}
