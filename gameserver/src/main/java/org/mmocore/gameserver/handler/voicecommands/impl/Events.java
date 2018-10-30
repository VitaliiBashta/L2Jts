package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class Events implements IVoicedCommandHandler {
    private final String[] commandList = {"events"};

    @Override
    public String[] getVoicedCommandList() {
        return commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        return true;
    }
}