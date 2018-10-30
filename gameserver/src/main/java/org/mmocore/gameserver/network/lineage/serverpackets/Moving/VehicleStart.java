package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Boat;

public class VehicleStart extends GameServerPacket {
    private final int objectId;
    private final int state;

    public VehicleStart(final Boat boat) {
        objectId = boat.getObjectId();
        state = boat.getRunState();
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeD(state);
    }
}