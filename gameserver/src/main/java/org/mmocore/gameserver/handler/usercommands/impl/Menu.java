package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 * custom command 1004 /menu
 */
public class Menu implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {1004};

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (id != COMMAND_IDS[0] || !AllSettingsConfig.ALT_ENABLE_MENU_COMMAND) {
            return false;
        }

        if (activeChar.isMovementDisabled() || activeChar.isAfraid() || activeChar.isActionsDisabled() || activeChar.isInOlympiadMode() || activeChar.isInDuel() || activeChar.getTeam() != TeamType.NONE) {
            activeChar.sendMessage(activeChar.isLangRus() ? "В вашем состоянии, невозможно использовать эту команду!" : "In your state, you can not use this command!");
            return false;
        }

        final HtmlMessage html = new HtmlMessage(5);
        html.setFile("command/menu.htm");
        html.replace("%PremiumPoints%", String.valueOf(activeChar.getPremiumAccountComponent().getPremiumPoints()));
        activeChar.sendPacket(html);

        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}