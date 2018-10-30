package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.model.entity.olympiad.NobleSelector;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

public class SelectorTest implements IVoicedCommandHandler {
    private final String[] _commandList = {"select"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        if (!player.isGM()) {
            player.sendMessage(new CustomMessage("common.command404"));
            return false;
        }

        if (args == null) {
            return false;
        }

        int val, count = 0;
        final String[] params = args.split(" ");
        if (params.length < 2 || params.length > 50) {
            return false;
        }

        final NobleSelector<Integer> s = new NobleSelector<>(params.length);
        for (final String param : params) {
            try {
                val = Integer.parseInt(param);
                s.add(count++, val);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid number");
                return false;
            }
        }

        while (s.size() > 1) {
            s.reset();
            player.sendMessage(Integer.toString(s.testSelect()) + ' ' + Integer.toString(s.testSelect()));
        }

        return true;
    }
}
