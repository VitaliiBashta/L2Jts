package org.mmocore.authserver.network.gamecomm;

import org.mmocore.authserver.network.gamecomm.gs2as.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


public class PacketHandler {
    private static final Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(GameServer gs, ByteBuffer buf) {
        ReceivablePacket packet = null;

        int id = buf.get() & 0xff;

        if (!gs.isAuthed()) {
            switch (id) {
                case 0x00:
                    packet = new AuthRequest();
                    break;
                default:
                    _log.error("Received unknown packet: " + Integer.toHexString(id));
            }
        } else {
            switch (id) {
                case 0x01:
                    packet = new OnlineStatus();
                    break;
                case 0x02:
                    packet = new PlayerAuthRequest();
                    break;
                case 0x03:
                    packet = new PlayerInGame();
                    break;
                case 0x04:
                    packet = new PlayerLogout();
                    break;
                case 0x05:
                    packet = new SetAccountInfo();
                    break;
                case 0x06:
                    packet = new RequestPlayerGamePoint();
                    break;
                case 0x07:
                    packet = new RequestPlayerGamePointDecrease();
                    break;
                case 0x08:
                    packet = new RequestReportPoint();
                    break;
                case 0x09:
                    packet = new ResetAllPlayerReportPoints();
                    break;
                case 0x10:
                    packet = new BonusRequest();
                    break;
                case 0x11:
                    packet = new ChangeAccessLevel();
                    break;
                case 0x12:
                    packet = new CheckEmailRequest();
                    break;
                case 0x13:
                    packet = new UpdateEmailRequest();
                    break;
                case 0x14:
                    packet = new RequestPlayerGamePointIncrease();
                    break;
                case 0x15:
                    packet = new RequestAccountUpdate();
                    break;
                case 0xff:
                    packet = new PingResponse();
                    break;
                default:
                    _log.error("Received unknown packet: " + Integer.toHexString(id));
            }
        }

        return packet;
    }
}