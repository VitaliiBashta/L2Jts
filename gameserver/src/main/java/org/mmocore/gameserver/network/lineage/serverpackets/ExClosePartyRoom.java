package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExClosePartyRoom extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExClosePartyRoom();

    @Override
    protected void writeData() {
    }
}