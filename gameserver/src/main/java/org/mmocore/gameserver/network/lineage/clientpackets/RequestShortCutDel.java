package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

public class RequestShortCutDel extends L2GameClientPacket {
    private int _slot;
    private int _page;

    /**
     * packet type id 0x3F
     * format:		cd
     */
    @Override
    protected void readImpl() {
        final int id = readD();
        _slot = id % 12;
        _page = id / 12;
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        // client dont needs confirmation. this packet is just to inform the server
        activeChar.getShortCutComponent().deleteShortCut(_slot, _page);
    }
}