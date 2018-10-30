package org.mmocore.gameserver.handler.telnetcommands.impl;


import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.world.World;

import java.util.LinkedHashSet;
import java.util.Set;


public class TelnetSay implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetSay() {
        _commands.add(new TelnetCommand("announce", "ann") {
            @Override
            public String getUsage() {
                return "announce <text>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0) {
                    return null;
                }

                AnnouncementUtils.announceToAll(args[0]);

                return "Announcement sent.\n";
            }
        });

        _commands.add(new TelnetCommand("message", "msg") {
            @Override
            public String getUsage() {
                return "message <player> <text>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length < 2) {
                    return null;
                }

                final Player player = World.getPlayer(args[0]);
                if (player == null) {
                    return "Player not found.\n";
                }

                final Say2 cs = new Say2(0, ChatType.TELL, "[Admin]", args[1], null);
                player.sendPacket(cs);

                return "Message sent.\n";
            }

        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}