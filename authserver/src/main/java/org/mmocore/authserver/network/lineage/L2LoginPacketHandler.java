package org.mmocore.authserver.network.lineage;

import org.mmocore.authserver.network.lineage.L2LoginClient.LoginClientState;
import org.mmocore.authserver.network.lineage.clientpackets.*;
import org.mmocore.commons.net.nio.impl.IPacketHandler;
import org.mmocore.commons.net.nio.impl.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public final class L2LoginPacketHandler implements IPacketHandler<L2LoginClient> {
    private final static Logger LOGGER = LoggerFactory.getLogger(L2LoginPacketHandler.class);

    @Override
    public ReceivablePacket<L2LoginClient> handlePacket(ByteBuffer buf, L2LoginClient client) {
        int opcode = buf.get() & 0xFF;

        ReceivablePacket<L2LoginClient> packet = null;
        LoginClientState state = client.getState();

        switch (state) {
            case CONNECTED:
                if (opcode == 0x07) {
                    packet = new AuthGameGuard();
                }
                break;
            case AUTHED_GG:
                if (opcode == 0x00) {
                    packet = new RequestAuthLogin();
                } else if (opcode == 0x0B) {
                    packet = new RequestCMDLogin();
                }
                break;
            case AUTHED:
                if (opcode == 0x05) {
                    packet = new RequestServerList();
                } else if (opcode == 0x02) {
                    packet = new RequestServerLogin();
                } else if (opcode == 0x06) {
                    //packet = new RequestSubmitCardNo();
                }
                break;
            default:
                LOGGER.debug("Client: " + client.getIpAddress() + " sended incorect packet: " + opcode);
                break;
        }
        return packet;
    }
}