package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminIP implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanBan) {
            return false;
        }

        switch (command) {
            case admin_charip:
                if (wordList.length != 2) {
                    activeChar.sendAdminMessage("Command syntax: //charip <char_name>");
                    activeChar.sendAdminMessage(" Gets character's IP.");
                    break;
                }

                final Player pl = World.getPlayer(wordList[1]);

                if (pl == null) {
                    activeChar.sendAdminMessage("Character " + wordList[1] + " not found.");
                    break;
                }

                final String ip_adr = pl.getIP();
                if ("<not connected>".equalsIgnoreCase(ip_adr)) {
                    activeChar.sendAdminMessage("Character " + wordList[1] + " not found.");
                    break;
                }

                activeChar.sendAdminMessage("Character's IP: " + ip_adr);
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
        admin_charip
    }
}