package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExRedSky extends GameServerPacket {
    private final int duration;

    public ExRedSky(final int duration) {
        this.duration = duration;
    }

    @Override
    protected final void writeData() {
        writeD(duration);
    }
}