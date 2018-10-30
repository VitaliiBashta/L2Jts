package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.AppearanceComponent;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Util;

/**
 * Support for /resetname command
 */
public class ResetName implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {117};

    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_IDS[0] != id) {
            return false;
        }

        final String oldTitle = activeChar.getPlayerVariables().get(PlayerVariables.OLD_TITLE);
        if (oldTitle != null) {
            activeChar.getAppearanceComponent().setTitleColor(AppearanceComponent.DEFAULT_TITLE_COLOR);
            if (Util.checkIsAllowedTitle(oldTitle))
                activeChar.setTitle(oldTitle);
            activeChar.broadcastUserInfo(true);
            return true;
        }
        return false;
    }

    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
