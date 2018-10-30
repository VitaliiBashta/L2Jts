package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.Player;

/**
 * This class handles following admin commands: - gm = turns gm mode on/off
 */
public class AdminGm implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        // TODO зачем отключено?
        if (Boolean.TRUE) {
            return false;
        }

        if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        }

        switch (command) {
            case admin_gm:
                handleGm(activeChar);
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleGm(final Player activeChar) {
        if (activeChar.isGM()) {
            activeChar.getPlayerAccess().IsGM = false;
            activeChar.sendAdminMessage("You no longer have GM status.");
        } else {
            activeChar.getPlayerAccess().IsGM = true;
            activeChar.sendAdminMessage("You have GM status now.");
        }
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_gm
    }
}