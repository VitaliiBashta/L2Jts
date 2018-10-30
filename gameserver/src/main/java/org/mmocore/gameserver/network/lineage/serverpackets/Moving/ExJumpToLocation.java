package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

public class ExJumpToLocation extends GameServerPacket {
    private final int objectId;
    private final Location current;
    private final Location destination;

    public ExJumpToLocation(final int objectId, final Location from, final Location to) {
        this.objectId = objectId;
        this.current = from;
        this.destination = to;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);

        writeD(destination.x);
        writeD(destination.y);
        writeD(destination.z);

        writeD(current.x);
        writeD(current.y);
        writeD(current.z);
    }
}