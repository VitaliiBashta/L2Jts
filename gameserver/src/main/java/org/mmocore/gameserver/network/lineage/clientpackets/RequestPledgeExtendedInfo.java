package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

/**
 * Format: (c) S
 * S: pledge name?
 */
public class RequestPledgeExtendedInfo extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        String _name = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isGM()) {
            activeChar.sendMessage("RequestPledgeExtendedInfo");
        }

        // TODO this
    }
}