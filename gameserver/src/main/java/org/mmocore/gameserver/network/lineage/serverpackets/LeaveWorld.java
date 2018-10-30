package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class LeaveWorld extends GameServerPacket {
    public static final GameServerPacket STATIC = new LeaveWorld();

    @Override
    protected final void writeData() {
    }
}