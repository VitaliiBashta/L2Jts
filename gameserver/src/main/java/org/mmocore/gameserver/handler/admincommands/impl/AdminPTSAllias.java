package org.mmocore.gameserver.handler.admincommands.impl;

import org.jts.dataparser.data.holder.BuilderCmdAliasHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.object.Player;

public class AdminPTSAllias implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar) throws InstantiationException, IllegalAccessException {
        return false;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return null;
    }

    @Override
    public String[] getAdminCommandString() {
        return BuilderCmdAliasHolder.getInstance().getAllStringCommands();
    }

}