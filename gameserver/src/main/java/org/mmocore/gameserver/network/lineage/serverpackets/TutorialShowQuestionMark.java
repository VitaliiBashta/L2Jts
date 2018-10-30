package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class TutorialShowQuestionMark extends GameServerPacket {
    /**
     * После клика по знаку вопроса клиент попросит html-ку с этим номером.
     */
    private final int number;

    public TutorialShowQuestionMark(final int number) {
        this.number = number;
    }

    @Override
    protected final void writeData() {
        writeD(number);
    }
}