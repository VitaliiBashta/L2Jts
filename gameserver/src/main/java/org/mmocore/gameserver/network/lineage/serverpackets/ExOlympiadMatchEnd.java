package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExOlympiadMatchEnd extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExOlympiadMatchEnd();

    @Override
    protected void writeData() {
    }
}