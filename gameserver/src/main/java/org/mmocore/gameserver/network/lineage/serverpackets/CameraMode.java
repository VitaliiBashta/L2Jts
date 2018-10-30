package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class CameraMode extends GameServerPacket {
    final int mode;

    /**
     * Forces client camera mode change
     *
     * @param mode 0 - third person cam
     *             1 - first person cam
     */
    public CameraMode(final int mode) {
        this.mode = mode;
    }

    @Override
    protected final void writeData() {
        writeD(mode);
    }
}