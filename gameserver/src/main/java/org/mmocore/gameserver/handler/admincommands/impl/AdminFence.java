package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.FenceBuilderManager;
import org.mmocore.gameserver.object.Player;

public class AdminFence implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
        }

        switch (command) {
            case admin_fence:
                if (wordList.length < 5) {
                    activeChar.sendAdminMessage("Not all arguments was set");
                    activeChar.sendAdminMessage("USAGE: //fence type width height size");
                    return false;
                }
                FenceBuilderManager.getInstance().spawnFence(activeChar, Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]), Integer.parseInt(wordList[3]), Integer.parseInt(wordList[4]));
                break;
            case admin_delallspawned:
                FenceBuilderManager.getInstance().deleteAllFences(activeChar);
                break;
            case admin_dellastspawned:
                FenceBuilderManager.getInstance().deleteLastFence(activeChar);
                break;
            case admin_fbuilder:
            case admin_fb:
                FenceBuilderManager.getInstance().fenceMenu(activeChar);
                break;
            case admin_fbx:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Not all arguments was set");
                    activeChar.sendAdminMessage("USAGE: //fbx type");
                    return false;
                }
                FenceBuilderManager.getInstance().changeFenceType(activeChar, Integer.parseInt(wordList[1]));
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
        admin_fence,
        admin_delallspawned,
        admin_dellastspawned,
        admin_fbuilder,
        admin_fb,
        admin_fbx
    }
}
