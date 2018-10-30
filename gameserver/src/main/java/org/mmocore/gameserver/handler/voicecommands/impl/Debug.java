package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

public class Debug implements IVoicedCommandHandler {
    private final String[] _commandList = {"debug"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        if (!AllSettingsConfig.ALT_DEBUG_ENABLED) {
            return false;
        }

        if (player.isDebug()) {
            player.setDebug(false);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Disabled"));
        } else {
            player.setDebug(true);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Enabled"));
        }
        return true;
    }
}
