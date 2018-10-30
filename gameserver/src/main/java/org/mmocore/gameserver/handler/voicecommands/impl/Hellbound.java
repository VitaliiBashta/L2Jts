package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.object.Player;

public class Hellbound implements IVoicedCommandHandler {
    private final String[] _commandList = {"hellbound"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player activeChar, final String target) {
        if ("hellbound".equals(command)) {
            activeChar.sendMessage("Hellbound level: " + HellboundManager.getHellboundLevel());
            activeChar.sendMessage("Confidence: " + HellboundManager.getConfidence());
        }
        return false;
    }
}
