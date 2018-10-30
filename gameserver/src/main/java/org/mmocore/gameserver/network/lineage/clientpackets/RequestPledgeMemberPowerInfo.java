package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeReceivePowerInfo;
import org.mmocore.gameserver.object.Player;

public class RequestPledgeMemberPowerInfo extends L2GameClientPacket {
    private int _pledgeType;
    private String _target;

    @Override
    protected void readImpl() {
        _pledgeType = readD();
        _target = readS(ServerConfig.CNAME_MAXLEN);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final SubUnit subUnit = activeChar.getClan() == null ? null : activeChar.getClan().getSubUnit(_pledgeType);
        if (subUnit != null) {
            final UnitMember cm = subUnit.getUnitMember(_target);
            if (cm != null) {
                activeChar.sendPacket(new PledgeReceivePowerInfo(cm));
            }
        }
    }
}