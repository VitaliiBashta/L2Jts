package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

public class MoveToPawn extends GameServerPacket {
    private final int chaId;
    private final int targetId;
    private final int distance;
    private final int x;
    private final int y;
    private final int z;
    private final int tx;
    private final int ty;
    private final int tz;

    public MoveToPawn(final Creature cha, final Creature target, final int distance) {
        chaId = cha.getObjectId();
        targetId = target.getObjectId();
        this.distance = distance;
        x = cha.getX();
        y = cha.getY();
        z = cha.getZ();
        tx = target.getX();
        ty = target.getY();
        tz = target.getZ();
    }

    @Override
    protected final void writeData() {
        writeD(chaId);
        writeD(targetId);
        writeD(distance);

        writeD(x);
        writeD(y);
        writeD(z);

        writeD(tx);
        writeD(ty);
        writeD(tz);
    }
}