package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExNavitAdventPointInfo extends GameServerPacket {
    private final int points;

    public ExNavitAdventPointInfo(final int points) {
        this.points = points;
    }

    @Override
    protected final void writeData() {
        writeD(points);
    }
}