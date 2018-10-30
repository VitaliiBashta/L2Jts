package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class GMHide extends GameServerPacket {
    private final int obj_id;

    public GMHide(final int id) {
        obj_id = id; //TODO хз чей id должен посылатся, нужно эксперементировать
    }

    @Override
    protected void writeData() {
        writeD(obj_id);
    }
}