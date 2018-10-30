package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExShowSeedMapInfo;
import org.mmocore.gameserver.object.Player;

public class RequestExSeedPhase extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        sendPacket(new ExShowSeedMapInfo());
    }
}