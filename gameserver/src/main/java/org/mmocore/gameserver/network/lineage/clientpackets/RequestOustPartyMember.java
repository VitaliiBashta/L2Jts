package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.DimensionalRift;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

public class RequestOustPartyMember extends L2GameClientPacket {
    //Format: cS
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

        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("Вы не можете сейчас выйти из группы.");//TODO [G1ta0] custom message
            return;
        }

        final Player member = party.getPlayerByName(_name);

        if (member == activeChar) {
            activeChar.sendActionFailed();
            return;
        }

        if (member == null) {
            activeChar.sendActionFailed();
            return;
        }

        final Reflection r = party.getReflection();

        if (r instanceof DimensionalRift && member.getReflection() == r) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift"));
        } else if (r != null && !(r instanceof DimensionalRift)) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon"));
        } else {
            party.removePartyMember(member, true);
        }
    }
}