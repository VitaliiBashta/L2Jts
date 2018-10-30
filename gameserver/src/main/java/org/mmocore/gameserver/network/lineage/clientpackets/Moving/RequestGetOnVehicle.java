package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.data.BoatHolder;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class RequestGetOnVehicle extends L2GameClientPacket {
    private final Location _loc = new Location();
    private int _objectId;

    /**
     * packet type id 0x53
     * format:      cdddd
     */
    @Override
    protected void readImpl() {
        _objectId = readD();
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Boat boat = BoatHolder.getInstance().getBoat(_objectId);
        if (boat == null) {
            return;
        }

        player._stablePoint = boat.getCurrentWay().getReturnLoc();
        boat.addPlayer(player, _loc);
    }
}