package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.utils.Location;

public class VehicleInfo extends GameServerPacket {
    private final int boatObjectId;
    private final Location loc;

    public VehicleInfo(final Boat boat) {
        boatObjectId = boat.getObjectId();
        loc = boat.getLoc();
    }

    @Override
    protected final void writeData() {
        writeD(boatObjectId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(loc.h);
    }
}