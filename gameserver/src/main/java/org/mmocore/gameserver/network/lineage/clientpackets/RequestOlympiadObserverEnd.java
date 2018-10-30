package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestOlympiadObserverEnd extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getObserverMode() == Player.OBSERVER_STARTED) {
            if (activeChar.getOlympiadObserveGame() != null) {
                activeChar.leaveOlympiadObserverMode(true);
            }
        }
    }
}