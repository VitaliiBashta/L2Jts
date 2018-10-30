package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.utils.Location;

public class VehicleDeparture extends GameServerPacket {
    private final int moveSpeed;
    private final int rotationSpeed;
    private final int boatObjId;
    private final Location loc;

    public VehicleDeparture(final Boat boat) {
        boatObjId = boat.getObjectId();
        moveSpeed = boat.getMoveSpeed();
        rotationSpeed = boat.getRotationSpeed();
        loc = boat.getDestination();
    }

    @Override
    protected final void writeData() {
        writeD(boatObjId);
        writeD(moveSpeed);
        writeD(rotationSpeed);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
    }
}