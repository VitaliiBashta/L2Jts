package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket {
    // format: cS

    private String _bypass = null;

    @Override
    protected void readImpl() {
        _bypass = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.processQuestEvent(255, _bypass, null);
    }
}