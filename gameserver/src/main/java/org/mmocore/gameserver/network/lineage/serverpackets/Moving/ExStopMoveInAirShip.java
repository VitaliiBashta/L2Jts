package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class ExStopMoveInAirShip extends GameServerPacket {
    private final int char_id;
    private final int boat_id;
    private final int char_heading;
    private final Location loc;

    public ExStopMoveInAirShip(final Player cha) {
        char_id = cha.getObjectId();
        boat_id = cha.getBoat().getObjectId();
        this.loc = cha.getInBoatPosition();
        char_heading = cha.getHeading();
    }

    @Override
    protected final void writeData() {
        writeD(char_id);
        writeD(boat_id);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(char_heading);
    }
}