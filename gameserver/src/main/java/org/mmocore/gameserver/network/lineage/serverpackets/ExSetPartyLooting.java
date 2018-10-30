package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExSetPartyLooting extends GameServerPacket {
    private final int result;
    private final int mode;

    public ExSetPartyLooting(final int result, final int mode) {
        this.result = result;
        this.mode = mode;
    }

    @Override
    protected void writeData() {
        writeD(result);
        writeD(mode);
    }
}
