package org.mmocore.gameserver.network.authcomm;

import org.mmocore.gameserver.network.authcomm.as2gs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class PacketHandler {
    private static final Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(final ByteBuffer buf) {
        ReceivablePacket packet = null;

        final int id = buf.get() & 0xff;

        switch (id) {
            case 0x00:
                packet = new AuthResponse();
                break;
            case 0x01:
                packet = new LoginServerFail();
                break;
            case 0x02:
                packet = new PlayerAuthResponse();
                break;
            case 0x03:
                packet = new KickPlayer();
                break;
            case 0x04:
                packet = new GetAccountInfo();
                break;
            case 0x05:
                packet = new CheckEmailResponse();
                break;
            case 0x06:
                packet = new PlayerGamePoint();
                break;
            case 0x07:
                packet = new PlayerGamePointDecrease();
                break;
            case 0x08:
                packet = new ReportPointRecent();
                break;
            case 0xff:
                packet = new PingRequest();
                break;
            default:
                _log.error("Received unknown packet: " + Integer.toHexString(id));
        }

        return packet;
    }
}
