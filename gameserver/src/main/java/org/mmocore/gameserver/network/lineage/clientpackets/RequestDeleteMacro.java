package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestDeleteMacro extends L2GameClientPacket {
    private int _id;

    /**
     * format:		cd
     */
    @Override
    protected void readImpl() {
        _id = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.getMacroComponent().deleteMacro(_id);
    }
}