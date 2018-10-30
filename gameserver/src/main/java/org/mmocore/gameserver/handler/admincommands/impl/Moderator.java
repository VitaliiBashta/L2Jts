package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AutoBan;

import java.util.StringTokenizer;

/**
 * Created by [STIGMATED] : 02.08.12 : 20:32
 * BNSworld
 */
public class Moderator implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        final StringTokenizer st = new StringTokenizer(fullString);

        if (!activeChar.getPlayerAccess().CanBanChat) {
            return false;
        }

        switch (command) {
            case admin_moder_chatban:
                try {
                    st.nextToken();
                    final String player = st.nextToken();
                    final String period = st.nextToken();

                    if (AutoBan.ChatBan(player, Integer.parseInt(period), activeChar.getName())) {
                        activeChar.sendAdminMessage("You ban chat for " + player + '.');
                    } else {
                        activeChar.sendAdminMessage("Can't find char " + player + '.');
                    }
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Command syntax: //chatban char_name period reason");
                }
                break;
            case admin_moderator: {
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/moderator.htm"));
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
        admin_moderator,
        admin_moder_chatban
    }
}
