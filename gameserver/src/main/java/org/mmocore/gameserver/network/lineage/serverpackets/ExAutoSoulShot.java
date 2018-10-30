package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExAutoSoulShot extends GameServerPacket {
    private final int itemId;
    private final boolean type;

    public ExAutoSoulShot(final int itemId, final boolean type) {
        this.itemId = itemId;
        this.type = type;
    }

    @Override
    protected final void writeData() {
        writeD(itemId);
        writeD(type ? 1 : 0);
    }
}