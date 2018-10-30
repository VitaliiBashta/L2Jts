package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.GmManager;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Player;

public class AdminGmChat implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanAnnounce) {
            return false;
        }

        switch (command) {
            case admin_gmchat:
                try {
                    String text = fullString.replaceFirst(Commands.admin_gmchat.name(), "");
                    Say2 cs = new Say2(0, ChatType.ALLIANCE, activeChar.getName(), text, null);
                    GmManager.broadcastToGMs(cs);
                } catch (StringIndexOutOfBoundsException ignored) {
                }
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
        admin_gmchat
    }
}