package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FinishRotating;
import org.mmocore.gameserver.object.Player;

/**
 * format:		cdd
 */
public class FinishRotatingC extends L2GameClientPacket {
    private int _degree;
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    protected void readImpl() {
        _degree = readD();
        _unknown = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.broadcastPacket(new FinishRotating(activeChar, _degree, 0));
    }
}