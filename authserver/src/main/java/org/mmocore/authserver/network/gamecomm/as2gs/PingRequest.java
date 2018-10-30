package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

public class PingRequest extends SendablePacket {
    @Override
    protected void writeImpl() {
        writeC(0xff);
    }
}