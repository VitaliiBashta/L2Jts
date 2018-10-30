package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Close the CommandChannel Information window
 */
public class ExMPCCClose extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExMPCCClose();

    @Override
    protected void writeData() {
    }
}
