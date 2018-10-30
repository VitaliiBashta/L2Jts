package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExNotifyBirthDay extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExNotifyBirthDay();

    @Override
    protected void writeData() {
    }
}