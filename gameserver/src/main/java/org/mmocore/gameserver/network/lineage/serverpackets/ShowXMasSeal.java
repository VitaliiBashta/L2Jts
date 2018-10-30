package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ShowXMasSeal extends GameServerPacket {
    private final int item;

    public ShowXMasSeal(final int item) {
        this.item = item;
    }

    @Override
    protected void writeData() {
        writeD(item);
    }
}