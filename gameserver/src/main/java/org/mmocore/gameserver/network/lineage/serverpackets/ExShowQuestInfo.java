package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExShowQuestInfo extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExShowQuestInfo();

    @Override
    protected final void writeData() {
    }
}