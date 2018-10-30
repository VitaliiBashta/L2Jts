package org.mmocore.gameserver.handler.admincommands;

import org.mmocore.gameserver.object.Player;

public interface IAdminCommandHandler {
    /**
     * this is the worker method that is called when someone uses an admin command.
     *
     * @param fullString TODO
     * @param activeChar
     * @param command
     * @return command success
     */
    boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar) throws InstantiationException, IllegalAccessException;

    /**
     * this method is called at initialization to register all the item ids automatically
     *
     * @return all known commands
     */
    Enum<?>[] getAdminCommandEnum();

    String[] getAdminCommandString();
}