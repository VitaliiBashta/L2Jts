package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestExBuySellUIClose extends L2GameClientPacket {
    @Override
    protected void runImpl() {
        // trigger
    }

    @Override
    protected void readImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.setBuyListId(0);
        activeChar.sendItemList(true);
    }
}