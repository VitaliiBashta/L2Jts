package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.Player;
import ru.akumu.smartguard.core.AdminMenu;
import ru.akumu.smartguard.wrappers.SmartPlayer;

/**
 * @author Akumu
 * @date 02.11.13
 */
public class SmartGuardMenu implements IAdminCommandHandler {
    private final AdminMenu menu = new AdminMenu();

    @Override
    public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar) {
        try {
            if (!activeChar.getPlayerAccess().SmartGuard || !activeChar.isGM())
                return false;
            String[] strings = fullString.split(" ");
            AdminMenu.Commands command = AdminMenu.Commands.valueOf(strings[0]);
            return menu.useAdminCommand(new SmartPlayer(activeChar), command, strings);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return AdminMenu.Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        return null;
    }
}
