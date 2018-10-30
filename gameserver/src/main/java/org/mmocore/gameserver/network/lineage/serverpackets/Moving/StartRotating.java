package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

public class StartRotating extends GameServerPacket {
    private final int charId;
    private final int degree;
    private final int side;
    private final int speed;

    public StartRotating(final Creature cha, final int degree, final int side, final int speed) {
        charId = cha.getObjectId();
        this.degree = degree;
        this.side = side;
        this.speed = speed;
    }

    @Override
    protected final void writeData() {
        writeD(charId);
        writeD(degree);
        writeD(side);
        writeD(speed);
    }
}