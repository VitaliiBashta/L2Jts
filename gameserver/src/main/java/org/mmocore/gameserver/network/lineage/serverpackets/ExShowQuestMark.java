package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExShowQuestMark extends GameServerPacket {
    private final int questId;

    public ExShowQuestMark(final int questId) {
        this.questId = questId;
    }

    @Override
    protected void writeData() {
        writeD(questId);
    }
}