package org.mmocore.gameserver.handler.voicecommands;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

public class VoicedCommandHandler extends AbstractHolder {
    private static final VoicedCommandHandler _instance = new VoicedCommandHandler();
    private final Map<String, IVoicedCommandHandler> _datatable = new HashMap<>();

    private VoicedCommandHandler() {
        ReflectionUtils.loadClassesWithConsumer("org.mmocore.gameserver.handler.voicecommands.impl", IVoicedCommandHandler.class,
                this::registerVoicedCommandHandler);
    }

    public static VoicedCommandHandler getInstance() {
        return _instance;
    }

    public void registerVoicedCommandHandler(final IVoicedCommandHandler handler) {
        final String[] ids = handler.getVoicedCommandList();
        for (final String element : ids) {
            _datatable.put(element, handler);
        }
    }

    public IVoicedCommandHandler getVoicedCommandHandler(final String voicedCommand) {
        String command = voicedCommand;
        if (voicedCommand.contains(" ")) {
            command = voicedCommand.substring(0, voicedCommand.indexOf(' '));
        }

        return _datatable.get(command);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    @Override
    public void clear() {
        _datatable.clear();
    }
}
