package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

public class ObserverStart extends GameServerPacket {
    // ddSS
    private final Location loc;

    public ObserverStart(final Location loc) {
        this.loc = loc;
    }

    @Override
    protected final void writeData() {
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeC(0x00);
        writeC(0xc0);
        writeC(0x00);
    }
}