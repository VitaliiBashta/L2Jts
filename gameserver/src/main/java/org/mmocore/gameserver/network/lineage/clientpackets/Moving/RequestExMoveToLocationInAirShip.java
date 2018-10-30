package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.data.BoatHolder;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class RequestExMoveToLocationInAirShip extends L2GameClientPacket {
    private final Location _pos = new Location();
    private final Location _originPos = new Location();
    private int _boatObjectId;

    @Override
    protected void readImpl() {
        _boatObjectId = readD();
        _pos.x = readD();
        _pos.y = readD();
        _pos.z = readD();
        _originPos.x = readD();
        _originPos.y = readD();
        _originPos.z = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Boat boat = BoatHolder.getInstance().getBoat(_boatObjectId);
        if (boat == null) {
            player.sendActionFailed();
            return;
        }

        if (player.isClanAirShipDriver()) {
            player.sendActionFailed();
            return;
        }

        boat.moveInBoat(player, _originPos, _pos);
    }
}