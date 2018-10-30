package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

/**
 * format   ddddd
 */
public class StopMove extends GameServerPacket {
    private final int objectId;
    private final int x;
    private final int y;
    private final int z;
    private final int heading;

    public StopMove(final Creature cha) {
        objectId = cha.getObjectId();
        x = cha.getX();
        y = cha.getY();
        z = cha.getZ();
        heading = cha.getHeading();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(x);
        writeD(y);
        writeD(z);
        writeD(heading);
    }
}