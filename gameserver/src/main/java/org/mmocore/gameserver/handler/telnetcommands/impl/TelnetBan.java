package org.mmocore.gameserver.handler.telnetcommands.impl;


import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.utils.AdminFunctions;

import java.util.LinkedHashSet;
import java.util.Set;


public class TelnetBan implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetBan() {
        _commands.add(new TelnetCommand("kick") {
            @Override
            public String getUsage() {
                return "kick <name>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0 || args[0].isEmpty()) {
                    return null;
                }

                if (AdminFunctions.kick(args[0], "telnet")) {
                    return "Player kicked.\n";
                } else {
                    return "Player not found.\n";
                }
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}