package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.GmManager;
import org.mmocore.gameserver.object.Player;

public class RequestGmList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar != null) {
            GmManager.sendListToPlayer(activeChar);
        }
    }
}