package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class ValidateLocationInVehicle extends GameServerPacket {
    private final int playerObjectId;
    private final int boatObjectId;
    private final Location loc;

    public ValidateLocationInVehicle(final Player player) {
        playerObjectId = player.getObjectId();
        boatObjectId = player.getBoat().getObjectId();
        loc = player.getInBoatPosition();
    }

    @Override
    protected final void writeData() {
        writeD(playerObjectId);
        writeD(boatObjectId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(loc.h);
    }
}