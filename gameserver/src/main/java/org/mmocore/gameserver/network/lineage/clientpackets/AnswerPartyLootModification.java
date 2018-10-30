package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Player;

public class AnswerPartyLootModification extends L2GameClientPacket {
    public int _answer;

    @Override
    protected void readImpl() {
        _answer = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Party party = activeChar.getParty();
        if (party != null) {
            party.answerLootChangeRequest(activeChar, _answer == 1);
        }
    }
}
