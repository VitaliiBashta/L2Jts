package org.mmocore.gameserver.handler.telnetcommands;

import org.mmocore.commons.utils.ReflectionUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kolobrodik
 */
public class TelnetCommandHandler {
    private static final TelnetCommandHandler instance = new TelnetCommandHandler();
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetCommandHandler() {
        ReflectionUtils.loadClassesWithConsumer("org.mmocore.gameserver.handler.telnetcommands.impl", ITelnetCommandHandler.class, this::registerTelnetCommandHandler);
    }

    public static TelnetCommandHandler getInstance() {
        return instance;
    }

    public void registerTelnetCommandHandler(final ITelnetCommandHandler handler) {
        _commands.addAll(handler.getCommands().stream().collect(Collectors.toList()));
    }

    public String tryHandleCommand(final String command, final String[] args) {
        final TelnetCommand cmd = getCommand(command);

        if (cmd == null) {
            return "Unknown command.\n";
        }

        String response = cmd.handle(args);
        if (response == null) {
            response = "usage:\n" + cmd.getUsage() + '\n';
        }

        return response;
    }

    public TelnetCommand getCommand(final String command) {
        for (final TelnetCommand cmd : _commands) {
            if (cmd.equals(command)) {
                return cmd;
            }
        }

        return null;
    }
}
