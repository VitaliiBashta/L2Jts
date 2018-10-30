package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

// format: cddddd
public class CannotMoveAnymoreInVehicle extends L2GameClientPacket {
    private final Location _loc = new Location();
    private int _boatid;

    @Override
    protected void readImpl() {
        _boatid = readD();
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
        _loc.h = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Boat boat = player.getBoat();
        if (boat != null && boat.getObjectId() == _boatid) {
            player.setInBoatPosition(_loc);
            player.setHeading(_loc.h);
            player.broadcastPacket(boat.inStopMovePacket(player));
        }
    }
}