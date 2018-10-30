package org.mmocore.gameserver.handler.telnetcommands.impl;


import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommandHandler;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Kolobrodik
 */
public class TelnetHelp implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetHelp() {
        _commands.add(new TelnetCommand("help", "h") {
            @Override
            public String getUsage() {
                return "help [command]";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Available commands:\n");
                    for (final TelnetCommand cmd : _commands) {
                        sb.append(cmd.getCommand()).append('\n');
                    }

                    return sb.toString();
                } else {
                    final TelnetCommand cmd = TelnetCommandHandler.getInstance().getCommand(args[0]);
                    if (cmd == null) {
                        return "Unknown command.\n";
                    }

                    return "usage:\n" + cmd.getUsage() + '\n';
                }
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}
