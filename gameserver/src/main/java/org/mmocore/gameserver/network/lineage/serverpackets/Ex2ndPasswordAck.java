package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class Ex2ndPasswordAck extends GameServerPacket {
    public static final int SUCCESS = 0x00;
    public static final int WRONG_PATTERN = 0x01;

    private final int response;
    private final int type;

    public Ex2ndPasswordAck(final int type, final int response) {
        this.response = response;
        this.type = type;
    }

    @Override
    protected void writeData() {
        writeC(type);
        writeD(response);
        writeD(0x00);
    }
}
