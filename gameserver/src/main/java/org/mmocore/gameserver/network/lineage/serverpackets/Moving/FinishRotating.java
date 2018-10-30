package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

public class FinishRotating extends GameServerPacket {
    private final int charId;
    private final int degree;
    private final int speed;

    public FinishRotating(final Creature player, final int degree, final int speed) {
        charId = player.getObjectId();
        this.degree = degree;
        this.speed = speed;
    }

    @Override
    protected final void writeData() {
        writeD(charId);
        writeD(degree);
        writeD(speed);
        writeD(0x00); //??
    }
}