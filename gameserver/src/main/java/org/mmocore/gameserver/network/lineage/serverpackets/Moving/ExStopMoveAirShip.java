package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.utils.Location;

public class ExStopMoveAirShip extends GameServerPacket {
    private final int boat_id;
    private final Location _loc;

    public ExStopMoveAirShip(final Boat boat) {
        boat_id = boat.getObjectId();
        _loc = boat.getLoc();
    }

    @Override
    protected final void writeData() {
        writeD(boat_id);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
    }
}