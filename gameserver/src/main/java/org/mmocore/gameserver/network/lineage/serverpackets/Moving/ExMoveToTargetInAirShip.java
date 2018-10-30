package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class ExMoveToTargetInAirShip extends GameServerPacket {
    private final int char_id;
    private final int boat_id;
    private final int target_id;
    private final int dist;
    private final Location loc;

    public ExMoveToTargetInAirShip(final Player cha, final Boat boat, final int targetId, final int dist, final Location origin) {
        char_id = cha.getObjectId();
        boat_id = boat.getObjectId();
        target_id = targetId;
        this.dist = dist;
        this.loc = origin;
    }

    @Override
    protected final void writeData() {
        writeD(char_id); // ID:%d
        writeD(target_id); // TargetID:%d
        writeD(dist); //Dist:%d
        writeD(loc.y); //OriginX:%d
        writeD(loc.z); //OriginY:%d
        writeD(loc.h); //OriginZ:%d
        writeD(boat_id); //AirShipID:%d
    }
}