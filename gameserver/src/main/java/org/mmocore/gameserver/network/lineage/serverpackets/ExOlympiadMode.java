package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExOlympiadMode extends GameServerPacket {
    // chc
    private final int mode;

    /**
     * @param _mode (0 = return, 3 = spectate)
     */
    public ExOlympiadMode(final int mode) {
        this.mode = mode;
    }

    @Override
    protected final void writeData() {
        writeC(mode);
    }
}