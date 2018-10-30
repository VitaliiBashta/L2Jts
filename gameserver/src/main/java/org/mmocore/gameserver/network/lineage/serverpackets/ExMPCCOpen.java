package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Opens the CommandChannel Information window
 */
public class ExMPCCOpen extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExMPCCOpen();

    @Override
    protected void writeData() {
    }
}