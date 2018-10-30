package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeReceiveMemberInfo;
import org.mmocore.gameserver.object.Player;

public class RequestPledgeMemberInfo extends L2GameClientPacket {
    // format: (ch)dS
    @SuppressWarnings("unused")
    private int _pledgeType;
    private String _target;

    @Override
    protected void readImpl() {
        _pledgeType = readD();
        _target = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        final Clan clan = activeChar.getClan();
        if (clan != null) {
            final UnitMember cm = clan.getAnyMember(_target);
            if (cm != null) {
                activeChar.sendPacket(new PledgeReceiveMemberInfo(cm));
            }
        }
    }
}