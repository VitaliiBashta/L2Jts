package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminTarget implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().CanViewChar) {
            return false;
        }

        try {
            final String targetName = wordList[1];
            final GameObject obj = World.getPlayer(targetName);
            if (obj != null && obj.isPlayer()) {
                obj.onAction(activeChar, false);
            } else {
                activeChar.sendAdminMessage("Player " + targetName + " not found");
            }
        } catch (IndexOutOfBoundsException e) {
            activeChar.sendAdminMessage("Please specify correct name.");
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
        admin_target
    }
}