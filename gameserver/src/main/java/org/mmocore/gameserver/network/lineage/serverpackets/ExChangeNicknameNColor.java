package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExChangeNicknameNColor extends GameServerPacket {
    private final int itemObjId;

    public ExChangeNicknameNColor(final int itemObjId) {
        this.itemObjId = itemObjId;
    }

    @Override
    protected void writeData() {
        writeD(itemObjId);
    }
}