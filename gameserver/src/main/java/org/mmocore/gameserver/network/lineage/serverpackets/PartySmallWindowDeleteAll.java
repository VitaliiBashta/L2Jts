package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PartySmallWindowDeleteAll extends GameServerPacket {
    public static final GameServerPacket STATIC = new PartySmallWindowDeleteAll();

    @Override
    protected final void writeData() {
    }
}