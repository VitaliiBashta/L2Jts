package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

public class ObserverEnd extends GameServerPacket {
    // ddSS
    private final Location loc;

    public ObserverEnd(final Location loc) {
        this.loc = loc;
    }

    @Override
    protected final void writeData() {
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
    }
}