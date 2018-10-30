package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ActionFail extends GameServerPacket {
    public static final GameServerPacket STATIC = new ActionFail();

    @Override
    protected final void writeData() {
    }
}