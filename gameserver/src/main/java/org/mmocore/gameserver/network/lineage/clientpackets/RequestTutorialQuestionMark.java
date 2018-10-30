package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestTutorialQuestionMark extends L2GameClientPacket {
    private int _number = 0;

    @Override
    protected void readImpl() {
        _number = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.processQuestEvent(255, "QM" + _number, null);
    }
}