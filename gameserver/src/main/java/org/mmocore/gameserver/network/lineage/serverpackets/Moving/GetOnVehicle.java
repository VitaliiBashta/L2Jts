package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class GetOnVehicle extends GameServerPacket {
    private final int playerObjectId;
    private final int boatObjectId;
    private final Location loc;

    public GetOnVehicle(final Player activeChar, final Boat boat, final Location loc) {
        this.loc = loc;
        playerObjectId = activeChar.getObjectId();
        boatObjectId = boat.getObjectId();
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