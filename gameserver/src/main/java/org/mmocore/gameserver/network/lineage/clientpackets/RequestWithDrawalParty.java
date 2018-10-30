package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.DimensionalRift;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

public class RequestWithDrawalParty extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Party party = activeChar.getParty();
        if (party == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("Вы не можете сейчас выйти из группы."); //TODO [G1ta0] custom message
            return;
        }

        final Reflection r = activeChar.getParty().getReflection();
        if (r instanceof DimensionalRift && activeChar.getReflection() == r) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestWithDrawalParty.Rift"));
        } else if (r != null && activeChar.isInCombat()) {
            activeChar.sendMessage("Вы не можете сейчас выйти из группы."); //TODO [G1ta0] custom message
        } else {
            activeChar.leaveParty();
        }
    }
}