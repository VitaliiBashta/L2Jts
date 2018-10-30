package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.StartRotating;
import org.mmocore.gameserver.object.Player;

/**
 * packet type id 0x5b
 * format:		cdd
 */
public class StartRotatingC extends L2GameClientPacket {
    private int _degree;
    private int _side;

    @Override
    protected void readImpl() {
        _degree = readD();
        _side = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.setHeading(_degree);
        activeChar.broadcastPacket(new StartRotating(activeChar, _degree, _side, 0));
    }
}