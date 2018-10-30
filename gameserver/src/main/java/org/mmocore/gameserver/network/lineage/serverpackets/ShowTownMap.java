package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ShowTownMap extends GameServerPacket {
    /**
     * Format: csdd
     */

    final String texture;
    final int x;
    final int y;

    public ShowTownMap(final String texture, final int x, final int y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
    }

    @Override
    protected final void writeData() {
        writeS(texture);
        writeD(x);
        writeD(y);
    }
}