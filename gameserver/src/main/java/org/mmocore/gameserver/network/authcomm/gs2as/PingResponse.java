package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class PingResponse extends SendablePacket {
    protected void writeImpl() {
        writeC(0xff);
        writeQ(System.currentTimeMillis());
    }
}