package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Player;

public class RequestPartyLootModification extends L2GameClientPacket {
    private byte _mode;

    @Override
    protected void readImpl() {
        _mode = (byte) readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_mode < 0 || _mode > Party.ITEM_ORDER_SPOIL) {
            return;
        }

        final Party party = activeChar.getParty();
        if (party == null || _mode == party.getLootDistribution() || party.getGroupLeader() != activeChar) {
            return;
        }

        party.requestLootChange(_mode);
    }
}
