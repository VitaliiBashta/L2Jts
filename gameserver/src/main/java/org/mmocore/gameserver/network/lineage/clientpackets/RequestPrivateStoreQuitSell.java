package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestPrivateStoreQuitSell extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.isInStoreMode() ||
                activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_SELL && activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_SELL_PACKAGE) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
        activeChar.standUp();
        activeChar.broadcastCharInfo();
    }
}