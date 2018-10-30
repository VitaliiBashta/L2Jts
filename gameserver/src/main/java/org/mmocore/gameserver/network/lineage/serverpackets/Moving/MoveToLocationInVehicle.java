package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class MoveToLocationInVehicle extends GameServerPacket {
    private final int playerObjectId;
    private final int boatObjectId;
    private final Location origin;
    private final Location destination;

    public MoveToLocationInVehicle(final Player cha, final Boat boat, final Location origin, final Location destination) {
        playerObjectId = cha.getObjectId();
        boatObjectId = boat.getObjectId();
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    protected final void writeData() {
        writeD(playerObjectId);
        writeD(boatObjectId);
        writeD(destination.x);
        writeD(destination.y);
        writeD(destination.z);
        writeD(origin.x);
        writeD(origin.y);
        writeD(origin.z);
    }
}