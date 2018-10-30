package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ServerClose extends GameServerPacket {
    public static final GameServerPacket STATIC = new ServerClose();

    @Override
    protected void writeData() {
    }
}