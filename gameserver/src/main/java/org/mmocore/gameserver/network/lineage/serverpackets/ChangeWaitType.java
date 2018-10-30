package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

/**
 * 0000: 3f 2a 89 00 4c 01 00 00 00 0a 15 00 00 66 fe 00    ?*..L........f..
 * 0010: 00 7c f1 ff ff                                     .|...
 * <p/>
 * format   dd ddd
 */
public class ChangeWaitType extends GameServerPacket {
    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;
    private final int objectId;
    private final int moveType;
    private final int x;
    private final int y;
    private final int z;

    public ChangeWaitType(final Creature cha, final int newMoveType) {
        objectId = cha.getObjectId();
        moveType = newMoveType;
        x = cha.getX();
        y = cha.getY();
        z = cha.getZ();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(moveType);
        writeD(x);
        writeD(y);
        writeD(z);
    }
}