package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExNavitAdventEffect extends GameServerPacket {
    private final int time;

    public ExNavitAdventEffect(final int time) {
        this.time = time;
    }

    @Override
    protected final void writeData() {
        writeD(time);
    }
}