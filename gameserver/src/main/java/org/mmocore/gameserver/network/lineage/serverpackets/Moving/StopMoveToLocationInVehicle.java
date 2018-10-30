package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class StopMoveToLocationInVehicle extends GameServerPacket {
    private final int boatObjectId;
    private final int playerObjectId;
    private final int heading;
    private final Location loc;

    public StopMoveToLocationInVehicle(final Player player) {
        boatObjectId = player.getBoat().getObjectId();
        playerObjectId = player.getObjectId();
        loc = player.getInBoatPosition();
        heading = player.getHeading();
    }

    @Override
    protected final void writeData() {
        writeD(playerObjectId);
        writeD(boatObjectId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(heading);
    }
}