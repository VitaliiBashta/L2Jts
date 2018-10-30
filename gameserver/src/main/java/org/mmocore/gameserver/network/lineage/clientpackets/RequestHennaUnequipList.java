package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.HennaUnequipList;
import org.mmocore.gameserver.object.Player;

public class RequestHennaUnequipList extends L2GameClientPacket {

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        int _symbolId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final HennaUnequipList he = new HennaUnequipList(activeChar);
        activeChar.sendPacket(he);
    }
}