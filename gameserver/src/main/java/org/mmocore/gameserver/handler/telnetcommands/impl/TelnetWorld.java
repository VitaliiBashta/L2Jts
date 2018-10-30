package org.mmocore.gameserver.handler.telnetcommands.impl;


import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.manager.GmManager;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class TelnetWorld implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetWorld() {
        _commands.add(new TelnetCommand("find") {

            @Override
            public String getUsage() {
                return "find <name>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0) {
                    return null;
                }

                final Iterable<Player> players = GameObjectsStorage.getPlayers();
                final Iterator<Player> itr = players.iterator();
                final StringBuilder sb = new StringBuilder();
                int count = 0;
                Player player;
                final Pattern pattern = Pattern.compile(args[0] + "\\S+", Pattern.CASE_INSENSITIVE);
                while (itr.hasNext()) {
                    player = itr.next();

                    if (pattern.matcher(player.getName()).matches()) {
                        count++;
                        sb.append(player).append('\n');
                    }
                }

                if (count == 0) {
                    sb.append("Player not found.").append('\n');
                } else {
                    sb.append("=================================================\n");
                    sb.append("Found: ").append(count).append(" players.").append('\n');
                }

                return sb.toString();
            }

        });
        _commands.add(new TelnetCommand("whois", "who") {

            @Override
            public String getUsage() {
                return "whois <name>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0) {
                    return null;
                }

                final Player player = GameObjectsStorage.getPlayer(args[0]);
                if (player == null) {
                    return "Player not found.\n";
                }

                final StringBuilder sb = new StringBuilder();

                sb.append("Name: .................... ").append(player.getName()).append('\n');
                sb.append("ID: ...................... ").append(player.getObjectId()).append('\n');
                sb.append("Account Name: ............ ").append(player.getAccountName()).append('\n');
                sb.append("IP: ...................... ").append(player.getIP()).append('\n');
                sb.append("Level: ................... ").append(player.getLevel()).append('\n');
                sb.append("Location: ................ ").append(player.getLoc()).append('\n');
                if (player.getClan() != null) {
                    sb.append("Clan: .................... ").append(player.getClan().getName()).append('\n');
                    if (player.getAlliance() != null) {
                        sb.append("Ally: .................... ").append(player.getAlliance().getAllyName()).append('\n');
                    }
                }
                sb.append("Offline: ................. ").append(player.isInOfflineMode()).append('\n');

                sb.append(player.toString()).append('\n');

                return sb.toString();
            }

        });
        _commands.add(new TelnetCommand("gmlist", "gms") {

            @Override
            public String getUsage() {
                return "gmlist";
            }

            @Override
            public String handle(final String[] args) {
                final List<Player> gms = GmManager.getAllGMs();

                if (gms.isEmpty())
                    return "GMs not found.\n";

                final StringBuilder sb = new StringBuilder();
                for (final Player gm : gms)
                    sb.append(gm).append('\n');

                sb.append("Found: ").append(gms.size()).append(" GMs.").append('\n');

                return sb.toString();
            }

        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}