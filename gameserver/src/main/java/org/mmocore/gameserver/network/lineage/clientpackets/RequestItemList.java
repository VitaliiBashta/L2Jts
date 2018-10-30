package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.StatusUpdate;
import org.mmocore.gameserver.object.Player;

public class RequestItemList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.getPlayerAccess().UseInventory || activeChar.isBlocked()) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendItemList(true);
        activeChar.sendStatusUpdate(false, false, StatusUpdate.CUR_LOAD);
    }
}