package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;

public class AdminHelpPage implements IAdminCommandHandler {
    public static void showHelpHtml(final Player activeChar, final String content) {
        final HtmlMessage adminReply = new HtmlMessage(5);
        adminReply.setHtml(content);
        activeChar.sendPacket(adminReply);
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        switch (command) {
            case admin_showhtml:
                if (wordList.length != 2) {
                    activeChar.sendAdminMessage("Usage: //showhtml <file>");
                    return false;
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/" + wordList[1]));
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
        admin_showhtml
    }
}