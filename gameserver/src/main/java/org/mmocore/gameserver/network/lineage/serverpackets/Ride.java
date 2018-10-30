package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class Ride extends GameServerPacket {
    private final int mountType;
    private final int id;
    private final int rideClassID;
    private final Location loc;

    public Ride(final Player cha) {
        id = cha.getObjectId();
        mountType = cha.getMountType();
        rideClassID = cha.getMountNpcId() + 1000000;
        loc = cha.getLoc();
    }

    @Override
    protected final void writeData() {
        writeD(id);
        writeD(mountType == 0 ? 0 : 1);
        writeD(mountType);
        writeD(rideClassID);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
    }
}