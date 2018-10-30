package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class AllianceCrest extends GameServerPacket {
    private final int crestId;
    private final byte[] data;

    public AllianceCrest(final int crestId, final byte[] data) {
        this.crestId = crestId;
        this.data = data;
    }

    @Override
    protected final void writeData() {
        writeD(crestId);
        writeD(data.length);
        writeB(data);
    }
}