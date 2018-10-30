package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.utils.Location;

public class ExMoveToLocationAirShip extends GameServerPacket {
    private final int objectId;
    private final Location origin;
    private final Location destination;

    public ExMoveToLocationAirShip(final Boat boat) {
        objectId = boat.getObjectId();
        origin = boat.getLoc();
        destination = boat.getDestination();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);

        writeD(destination.x);
        writeD(destination.y);
        writeD(destination.z);
        writeD(origin.x);
        writeD(origin.y);
        writeD(origin.z);
    }
}