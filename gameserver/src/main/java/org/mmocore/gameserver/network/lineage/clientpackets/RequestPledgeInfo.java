package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;

public class RequestPledgeInfo extends L2GameClientPacket {
    private int _clanId;

    @Override
    protected void readImpl() {
        _clanId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (_clanId < 1) //TODO[K] - не понятно, для чего тут это оставили...возможно есть некий косяк, но у нас то ИД считаются с 1го.
        {
            activeChar.sendActionFailed();
            return;
        }
        final Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            //Util.handleIllegalPlayerAction(activeChar, "RequestPledgeInfo[40]", "Clan data for clanId " + _clanId + " is missing", 1);
            //LOGGER.warn("Host " + getClient().getIpAddr() + " possibly sends fake packets. activeChar: " + activeChar);
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new PledgeInfo(clan));
    }
}