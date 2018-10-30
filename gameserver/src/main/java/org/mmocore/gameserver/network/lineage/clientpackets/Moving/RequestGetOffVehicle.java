package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.data.BoatHolder;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class RequestGetOffVehicle extends L2GameClientPacket {
    private final Location _location = new Location();
    // Format: cdddd
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _location.x = readD();
        _location.y = readD();
        _location.z = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Boat boat = BoatHolder.getInstance().getBoat(_objectId);
        if (boat == null || boat.isMoving) {
            player.sendActionFailed();
            return;
        }

        boat.oustPlayer(player, _location, false);
    }
}