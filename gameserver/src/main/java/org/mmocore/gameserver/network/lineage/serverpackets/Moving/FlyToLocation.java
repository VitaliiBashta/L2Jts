package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;

public class FlyToLocation extends GameServerPacket {
    private final int chaObjId;
    private final FlyType type;
    private final Location loc;
    private final Location destLoc;

    public FlyToLocation(final Creature cha, final Location destLoc, final FlyType type) {
        this.destLoc = destLoc;
        this.type = type;
        chaObjId = cha.getObjectId();
        loc = cha.getLoc();
    }

    @Override
    protected void writeData() {
        writeD(chaObjId);
        writeD(destLoc.x);
        writeD(destLoc.y);
        writeD(destLoc.z);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(type.ordinal());
    }

    public enum FlyType {
        THROW_UP,
        THROW_HORIZONTAL,
        DUMMY,
        CHARGE,
        NONE
    }
}