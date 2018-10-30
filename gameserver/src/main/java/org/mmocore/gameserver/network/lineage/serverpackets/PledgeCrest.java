package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeCrest extends GameServerPacket {
    private final int crestId;
    private final int crestSize;
    private final byte[] data;

    public PledgeCrest(final int crestId, final byte[] data) {
        this.crestId = crestId;
        this.data = data;
        crestSize = this.data.length;
    }

    @Override
    protected final void writeData() {
        writeD(crestId);
        if (data != null) {
            writeD(crestSize);
            writeB(data);
        } else
            writeD(0);
    }
}