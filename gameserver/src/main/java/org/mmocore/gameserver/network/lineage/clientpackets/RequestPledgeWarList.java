package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeReceiveWarList;
import org.mmocore.gameserver.object.Player;

public class RequestPledgeWarList extends L2GameClientPacket {
    // format: (ch)dd
    private int _type;
    private int _page;

    @Override
    protected void readImpl() {
        _page = readD();
        _type = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Clan clan = activeChar.getClan();
        if (clan != null) {
            activeChar.sendPacket(new PledgeReceiveWarList(clan, _type, _page));
        }
    }
}