package org.mmocore.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.object.Player;

public class AdminHellbound implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        switch (command) {
            case admin_hbadd:
                HellboundManager.addConfidence(Long.parseLong(wordList[1]));
                activeChar.sendAdminMessage("Added " + NumberUtils.toInt(wordList[1], 1) + " to Hellbound confidence");
                activeChar.sendAdminMessage("Hellbound confidence is now " + HellboundManager.getConfidence());
                break;
            case admin_hbsub:
                HellboundManager.reduceConfidence(Long.parseLong(wordList[1]));
                activeChar.sendAdminMessage("Reduced confidence by " + NumberUtils.toInt(wordList[1], 1));
                activeChar.sendAdminMessage("Hellbound confidence is now " + HellboundManager.getConfidence());
                break;
            case admin_hbset:
                HellboundManager.setConfidence(Long.parseLong(wordList[1]));
                activeChar.sendAdminMessage("Hellbound confidence is now " + HellboundManager.getConfidence());
                break;
            case admin_hbinfo:
                activeChar.sendAdminMessage("Hellbound level: " + HellboundManager.getHellboundLevel() + " confidence: " + HellboundManager.getConfidence());
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_hbadd,
        admin_hbsub,
        admin_hbset,
        admin_hbinfo
    }

}