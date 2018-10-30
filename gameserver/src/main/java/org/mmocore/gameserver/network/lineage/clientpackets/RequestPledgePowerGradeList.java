package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.RankPrivs;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgePowerGradeList;
import org.mmocore.gameserver.object.Player;

public class RequestPledgePowerGradeList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        final Clan clan = activeChar.getClan();
        if (clan != null) {
            final RankPrivs[] privs = clan.getAllRankPrivs();
            activeChar.sendPacket(new PledgePowerGradeList(privs));
        }
    }
}