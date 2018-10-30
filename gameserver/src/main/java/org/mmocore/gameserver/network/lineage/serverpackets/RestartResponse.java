package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class RestartResponse extends GameServerPacket {
    public static final RestartResponse OK = new RestartResponse(1), FAIL = new RestartResponse(0);
    private final String message;
    private final int param;

    public RestartResponse(final int param) {
        message = "bye";
        this.param = param;
    }

    @Override
    protected final void writeData() {
        writeD(param); //01-ok
        writeS(message);
    }
}