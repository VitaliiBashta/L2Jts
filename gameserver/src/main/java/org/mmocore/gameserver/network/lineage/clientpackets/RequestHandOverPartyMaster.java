package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

public class RequestHandOverPartyMaster extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Party party = activeChar.getParty();

        if (party == null || !activeChar.getParty().isLeader(activeChar)) {
            activeChar.sendActionFailed();
            return;
        }


        final Player member = party.getPlayerByName(_name);

        if (member == activeChar) {
            activeChar.sendPacket(SystemMsg.SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER);
            return;
        }

        if (member == null) {
            activeChar.sendPacket(SystemMsg.YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY);
            return;
        }

        activeChar.getParty().changePartyLeader(member);
    }
}