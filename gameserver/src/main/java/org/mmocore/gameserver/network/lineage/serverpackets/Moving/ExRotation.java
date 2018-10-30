package org.mmocore.gameserver.network.lineage.serverpackets.Moving;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExRotation extends GameServerPacket {
    private final int charObjId;
    private final int degree;

    public ExRotation(final int charId, final int degree) {
        charObjId = charId;
        this.degree = degree;
    }

    @Override
    protected void writeData() {
        writeD(charObjId);
        writeD(degree);
    }
}
