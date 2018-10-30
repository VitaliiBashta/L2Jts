package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExNavitAdventTimeChange extends GameServerPacket {
    private final int active;
    private final int time;

    public ExNavitAdventTimeChange(final boolean active, final int time) {
        this.active = active ? 1 : 0;
        this.time = 14400 - time;
    }

    @Override
    protected final void writeData() {
        writeC(active);
        writeD(time); // in minutes
    }
}
