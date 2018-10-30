package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPledgeCrestLarge extends GameServerPacket {
    private final int crestId;
    private final byte[] data;

    public ExPledgeCrestLarge(final int crestId, final byte[] data) {
        this.crestId = crestId;
        this.data = data;
    }

    @Override
    protected final void writeData() {
        writeD(0x00);
        writeD(crestId);
        writeD(data.length);
        writeB(data);
    }
}