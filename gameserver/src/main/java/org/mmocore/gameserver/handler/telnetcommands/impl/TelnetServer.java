package org.mmocore.gameserver.handler.telnetcommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.Shutdown;
import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

public class TelnetServer implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetServer() {
        _commands.add(new TelnetCommand("version", "ver") {
            @Override
            public String getUsage() {
                return "version";
            }

            @Override
            public String handle(final String[] args) {
                return "Rev." + GameServer.getInstance().getVersion().getRevisionNumber() + " Builded : " + GameServer.getInstance().getVersion().getBuildDate() + '\n';
            }
        });

        _commands.add(new TelnetCommand("uptime") {
            @Override
            public String getUsage() {
                return "uptime";
            }

            @Override
            public String handle(final String[] args) {
                return DurationFormatUtils.formatDurationHMS(ManagementFactory.getRuntimeMXBean().getUptime()) + '\n';
            }
        });

        _commands.add(new TelnetCommand("restart") {
            @Override
            public String getUsage() {
                return "restart <seconds>|now>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0) {
                    return null;
                }

                final StringBuilder sb = new StringBuilder();

                if (NumberUtils.isNumber(args[0])) {
                    final int val = NumberUtils.toInt(args[0]);
                    Shutdown.getInstance().schedule(val, Shutdown.RESTART);
                    sb.append("Server will restart in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
                    sb.append("Type \"abort\" to abort restart!\n");
                } else if ("now".equalsIgnoreCase(args[0])) {
                    sb.append("Server will restart now!\n");
                    Shutdown.getInstance().schedule(0, Shutdown.RESTART);
                } else {
                    final String[] hhmm = args[0].split(":");

                    final Calendar date = Calendar.getInstance();
                    final Calendar now = Calendar.getInstance();

                    date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hhmm[0]));
                    date.set(Calendar.MINUTE, hhmm.length > 1 ? Integer.parseInt(hhmm[1]) : 0);
                    date.set(Calendar.SECOND, 0);
                    date.set(Calendar.MILLISECOND, 0);
                    if (date.before(now)) {
                        date.roll(Calendar.DAY_OF_MONTH, true);
                    }

                    final int seconds = (int) (date.getTimeInMillis() / 1000L - now.getTimeInMillis() / 1000L);

                    Shutdown.getInstance().schedule(seconds, Shutdown.RESTART);
                    sb.append("Server will restart in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
                    sb.append("Type \"abort\" to abort restart!\n");
                }

                return sb.toString();
            }
        });

        _commands.add(new TelnetCommand("shutdown") {
            @Override
            public String getUsage() {
                return "shutdown <seconds>|now|<hh:mm>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length == 0) {
                    return null;
                }

                final StringBuilder sb = new StringBuilder();

                if (NumberUtils.isNumber(args[0])) {
                    final int val = NumberUtils.toInt(args[0]);
                    Shutdown.getInstance().schedule(val, Shutdown.SHUTDOWN);
                    sb.append("Server will shutdown in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
                    sb.append("Type \"abort\" to abort shutdown!\n");
                } else if ("now".equalsIgnoreCase(args[0])) {
                    sb.append("Server will shutdown now!\n");
                    Shutdown.getInstance().schedule(0, Shutdown.SHUTDOWN);
                } else {
                    final String[] hhmm = args[0].split(":");

                    final Calendar date = Calendar.getInstance();
                    final Calendar now = Calendar.getInstance();

                    date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hhmm[0]));
                    date.set(Calendar.MINUTE, hhmm.length > 1 ? Integer.parseInt(hhmm[1]) : 0);
                    date.set(Calendar.SECOND, 0);
                    date.set(Calendar.MILLISECOND, 0);
                    if (date.before(now)) {
                        date.roll(Calendar.DAY_OF_MONTH, true);
                    }

                    final int seconds = (int) (date.getTimeInMillis() / 1000L - now.getTimeInMillis() / 1000L);

                    Shutdown.getInstance().schedule(seconds, Shutdown.SHUTDOWN);
                    sb.append("Server will shutdown in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
                    sb.append("Type \"abort\" to abort shutdown!\n");
                }

                return sb.toString();
            }
        });

        _commands.add(new TelnetCommand("abort") {

            @Override
            public String getUsage() {
                return "abort";
            }

            @Override
            public String handle(final String[] args) {
                Shutdown.getInstance().cancel();
                return "Aborted.\n";
            }

        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}
