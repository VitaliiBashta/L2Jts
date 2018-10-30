package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class ExMoveToLocationInAirShip extends GameServerPacket {
    private final int char_id;
    private final int boat_id;
    private final Location origin;
    private final Location destination;

    public ExMoveToLocationInAirShip(final Player cha, final Boat boat, final Location origin, final Location destination) {
        char_id = cha.getObjectId();
        boat_id = boat.getObjectId();
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    protected final void writeData() {
        writeD(char_id);
        writeD(boat_id);

        writeD(destination.x);
        writeD(destination.y);
        writeD(destination.z);
        writeD(origin.x);
        writeD(origin.y);
        writeD(origin.z);
    }
}