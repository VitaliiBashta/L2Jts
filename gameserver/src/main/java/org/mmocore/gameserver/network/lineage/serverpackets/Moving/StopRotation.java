package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;

public class StopRotation extends L2GameServerPacket {
    private final int characterObjectId;
    private final int degree;
    private final int speed;

    public StopRotation(int characterObjectId, int degree, int speed) {
        this.characterObjectId = characterObjectId;
        this.degree = degree;
        this.speed = speed;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x61);
        writeD(characterObjectId);
        writeD(degree);
        writeD(speed);
        writeC(0); // ?
    }
}
