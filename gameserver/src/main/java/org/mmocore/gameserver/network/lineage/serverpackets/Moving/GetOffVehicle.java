package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class GetOffVehicle extends GameServerPacket {
    private final int playerObjectId;
    private final int boatObjectId;
    private final Location loc;

    public GetOffVehicle(final Player cha, final Boat boat, final Location loc) {
        playerObjectId = cha.getObjectId();
        boatObjectId = boat.getObjectId();
        this.loc = loc;
    }

    @Override
    protected final void writeData() {
        writeD(playerObjectId);
        writeD(boatObjectId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
    }
}