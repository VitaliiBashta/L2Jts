package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExMailArrived extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExMailArrived();

    @Override
    protected final void writeData() {
    }
}