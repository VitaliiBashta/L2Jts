package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.serverpackets.ExMPCCShowPartyMemberInfo;
import org.mmocore.gameserver.object.Player;

public class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
            return;
        }

        for (final Party party : activeChar.getParty().getCommandChannel().getParties()) {
            final Player leader = party.getGroupLeader();
            if (leader != null && leader.getObjectId() == _objectId) {
                activeChar.sendPacket(new ExMPCCShowPartyMemberInfo(party));
                break;
            }
        }
    }
}