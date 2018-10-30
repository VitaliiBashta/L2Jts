package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class OnlineStatus extends SendablePacket {
    private final boolean _online;

    public OnlineStatus(final boolean online) {
        _online = online;
    }

    protected void writeImpl() {
        writeC(0x01);
        writeC(_online ? 1 : 0);
    }
}
