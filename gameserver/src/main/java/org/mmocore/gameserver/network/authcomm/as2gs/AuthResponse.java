package org.mmocore.gameserver.network.authcomm.as2gs;


import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.authcomm.gs2as.OnlineStatus;
import org.mmocore.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthResponse extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(AuthResponse.class);

    private int _serverId;
    private String _serverName;

    @Override
    protected void readImpl() {
        _serverId = readC();
        _serverName = readS();
    }

    @Override
    protected void runImpl() {
        _log.info("Registered on authserver as " + _serverId + " [" + _serverName + ']');

        sendPacket(new OnlineStatus(true));

        final String[] accounts = AuthServerCommunication.getInstance().getAccounts();
        for (final String account : accounts) {
            sendPacket(new PlayerInGame(account));
        }
    }
}
