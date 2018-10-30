package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class AuthRequest extends SendablePacket {
    protected void writeImpl() {
        writeC(0x00);
        writeD(GameServer.AUTH_SERVER_PROTOCOL);
        writeC(ServerConfig.REQUEST_ID);
        writeC(ServerConfig.ACCEPT_ALTERNATE_ID ? 0x01 : 0x00);
        writeD(ServerConfig.AUTH_SERVER_SERVER_TYPE);
        writeD(ServerConfig.AUTH_SERVER_AGE_LIMIT);
        writeC(ServerConfig.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
        writeC(ServerConfig.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
        writeC(ServerConfig.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
        writeS(ServerConfig.EXTERNAL_HOSTNAME);
        writeS(ServerConfig.INTERNAL_HOSTNAME);
        writeH(1);
        writeH(ServerConfig.PORTS_GAME);
        writeD(ServerConfig.MAXIMUM_ONLINE_USERS);
    }
}
