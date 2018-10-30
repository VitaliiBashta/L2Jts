package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.commons.net.utils.NetInfo;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.network.authcomm.channel.AbstractServerChannel;

import java.util.List;

public class AuthRequest2 extends SendablePacket {
    private final AbstractServerChannel _channel;

    public AuthRequest2(final AbstractServerChannel abstractServerChannel) {
        _channel = abstractServerChannel;
    }

    @Override
    protected void writeImpl() {
        writeC(0x00);
        writeD(GameServer.AUTH_SERVER_PROTOCOL_IPСONFIG);
        writeC(_channel.getId());
        writeC(ServerConfig.ACCEPT_ALTERNATE_ID ? 0x01 : 0x00);
        writeD(ServerConfig.AUTH_SERVER_SERVER_TYPE);
        writeD(ServerConfig.AUTH_SERVER_AGE_LIMIT);
        writeC(ServerConfig.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
        writeC(ServerConfig.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
        writeC(ServerConfig.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
        writeD(ServerConfig.MAXIMUM_ONLINE_USERS);

        final List<NetInfo> infos = _channel.getInfos();
        writeD(infos.size());
        for (final NetInfo info : infos) {
            writeD(info.net().netmask());
            writeD(info.net().address());
            writeS(info.host());
            writeH(1);
            writeH(info.port());
        }
    }
}
