package org.mmocore.gameserver.handler.telnetcommands.impl;

import com.sun.management.HotSpotDiagnosticMXBean;
import org.apache.commons.io.FileUtils;
import org.infinispan.Cache;
import org.infinispan.stats.Stats;
import org.mmocore.commons.database.dao.JdbcEntityStats;
import org.mmocore.commons.lang.StatsUtils;
import org.mmocore.commons.net.nio.impl.SelectorThread;
import org.mmocore.commons.threading.RunnableStatsManager;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AiConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.geoengine.pathfind.PathFindBuffers;
import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.taskmanager.AiTaskManager;
import org.mmocore.gameserver.taskmanager.EffectTaskManager;
import org.mmocore.gameserver.utils.GameStats;

import javax.management.MBeanServer;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

public class TelnetPerfomance implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetPerfomance() {
        _commands.add(new TelnetCommand("pool", "p") {
            @Override
            public String getUsage() {
                return "pool [dump]";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                if (args.length == 0 || args[0].isEmpty()) {
                    sb.append(ThreadPoolManager.getInstance().getStats());
                } else if ("dump".equals(args[0]) || "d".equals(args[0])) {
                    try {
                        new File("stats").mkdir();
                        FileUtils.writeStringToFile(new File("stats/RunnableStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), RunnableStatsManager.getInstance().getStats().toString());
                        sb.append("Runnable stats saved.\n");
                    } catch (IOException e) {
                        sb.append("Exception: ").append(e.getMessage()).append("!\n");
                    }
                } else {
                    return null;
                }

                return sb.toString();
            }

        });

        _commands.add(new TelnetCommand("mem", "m") {
            @Override
            public String getUsage() {
                return "mem";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();
                sb.append(StatsUtils.getMemUsage());

                return sb.toString();
            }
        });

        _commands.add(new TelnetCommand("heap") {

            @Override
            public String getUsage() {
                return "heap [dump] <live>";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                if (args.length == 0 || args[0].isEmpty()) {
                    return null;
                } else if ("dump".equals(args[0]) || "d".equals(args[0])) {
                    try {
                        final boolean live = args.length == 2 && !args[1].isEmpty() && ("live".equals(args[1]) || "l".equals(args[1]));
                        new File("dumps").mkdir();
                        final String filename = "dumps/HeapDump" + (live ? "Live" : "") + '-' + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".hprof";

                        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                        final HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
                        bean.dumpHeap(filename, live);

                        sb.append("Heap dumped.\n");
                    } catch (IOException e) {
                        sb.append("Exception: ").append(e.getMessage()).append("!\n");
                    }
                } else {
                    return null;
                }

                return sb.toString();
            }

        });
        _commands.add(new TelnetCommand("threads", "t") {
            @Override
            public String getUsage() {
                return "threads [dump]";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                if (args.length == 0 || args[0].isEmpty()) {
                    sb.append(StatsUtils.getThreadStats());
                } else if ("dump".equals(args[0]) || "d".equals(args[0])) {
                    try {
                        new File("stats").mkdir();
                        FileUtils.writeStringToFile(new File("stats/ThreadsDump-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), StatsUtils.getThreadStats(true, true, true).toString());
                        sb.append("Threads stats saved.\n");
                    } catch (IOException e) {
                        sb.append("Exception: ").append(e.getMessage()).append("!\n");
                    }
                } else {
                    return null;
                }

                return sb.toString();
            }
        });

        _commands.add(new TelnetCommand("gc") {
            @Override
            public String getUsage() {
                return "gc";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();
                sb.append(StatsUtils.getGCStats());

                return sb.toString();
            }
        });

        _commands.add(new TelnetCommand("net", "ns") {
            @Override
            public String getUsage() {
                return "net";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                sb.append(SelectorThread.getStats());

                return sb.toString();
            }

        });

        _commands.add(new TelnetCommand("pathfind", "pfs") {

            @Override
            public String getUsage() {
                return "pathfind";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                sb.append(PathFindBuffers.getStats());

                return sb.toString();
            }

        });

        _commands.add(new TelnetCommand("dbstats", "ds") {

            @Override
            public String getUsage() {
                return "dbstats";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                sb.append("Basic database usage\n");
                sb.append("=================================================\n");
                sb.append("Connections").append('\n');
                sb.append("     total leased: ........................ ").append(DatabaseFactory.getInstance().getTotalLeased()).append('\n');
                sb.append("     total free: ........................ ").append(DatabaseFactory.getInstance().getTotalFree()).append('\n');
                sb.append("     total created connections: ........................ ").append(DatabaseFactory.getInstance().getTotalCreatedConnections()).append('\n');

                sb.append("Players").append('\n');
                sb.append("     Update: ...................... ").append(GameStats.getUpdatePlayerBase()).append('\n');

                Cache cache;
                Stats cacheStats;
                JdbcEntityStats entityStats;

                cache = ItemsDAO.getInstance().getCache();
                cacheStats = cache.getAdvancedCache().getStats();
                entityStats = ItemsDAO.getInstance().getStats();

                sb.append("Items").append('\n');
                sb.append("     getLoadCount: ................ ").append(entityStats.getLoadCount()).append('\n');
                sb.append("     getInsertCount: .............. ").append(entityStats.getInsertCount()).append('\n');
                sb.append("     getUpdateCount: .............. ").append(entityStats.getUpdateCount()).append('\n');
                sb.append("     getDeleteCount: .............. ").append(entityStats.getDeleteCount()).append('\n');

                sb.append("Cache").append('\n');
                sb.append("     size: ..................... ").append(cacheStats.getCurrentNumberOfEntries()).append('\n');
                sb.append("     stores: ................. ").append(cacheStats.getStores()).append('\n');
                sb.append("     retrievals: .............. ").append(cacheStats.getRetrievals()).append('\n');
                sb.append("     hits: ............. ").append(cacheStats.getHits()).append('\n');
                sb.append("     misses: ............. ").append(cacheStats.getMisses()).append('\n');
                sb.append("     removeHits: ............. ").append(cacheStats.getRemoveHits()).append('\n');
                sb.append("     removeMisses: ................. ").append(cacheStats.getRemoveMisses()).append('\n');
                sb.append("     evictions: .............. ").append(cacheStats.getEvictions()).append('\n');
                sb.append("     averageReadTime: ............. ").append(cacheStats.getAverageReadTime()).append('\n');
                sb.append("     averageWriteTime: ............. ").append(cacheStats.getAverageWriteTime()).append('\n');
                sb.append("     averageRemoveTime: ............. ").append(cacheStats.getAverageRemoveTime()).append('\n');
				/*
				sb.append("     getInMemorySize: ............. ").append(cacheStats.getInMemorySize()).append('\n');
				sb.append("     getOnDiskSize: ............... ").append(cacheStats.getOnDiskSize()).append('\n');
				*/
                sb.append("=================================================\n");

                cache = MailDAO.getInstance().getCache();
                cacheStats = cache.getAdvancedCache().getStats();
                entityStats = MailDAO.getInstance().getStats();

                sb.append("Mail").append('\n');
                sb.append("     getLoadCount: ................ ").append(entityStats.getLoadCount()).append('\n');
                sb.append("     getInsertCount: .............. ").append(entityStats.getInsertCount()).append('\n');
                sb.append("     getUpdateCount: .............. ").append(entityStats.getUpdateCount()).append('\n');
                sb.append("     getDeleteCount: .............. ").append(entityStats.getDeleteCount()).append('\n');

                sb.append("Cache").append('\n');
                sb.append("     size: ..................... ").append(cacheStats.getCurrentNumberOfEntries()).append('\n');
                sb.append("     stores: ................. ").append(cacheStats.getStores()).append('\n');
                sb.append("     retrievals: .............. ").append(cacheStats.getRetrievals()).append('\n');
                sb.append("     hits: ............. ").append(cacheStats.getHits()).append('\n');
                sb.append("     misses: ............. ").append(cacheStats.getMisses()).append('\n');
                sb.append("     removeHits: ............. ").append(cacheStats.getRemoveHits()).append('\n');
                sb.append("     removeMisses: ................. ").append(cacheStats.getRemoveMisses()).append('\n');
                sb.append("     evictions: .............. ").append(cacheStats.getEvictions()).append('\n');
                sb.append("     averageReadTime: ............. ").append(cacheStats.getAverageReadTime()).append('\n');
                sb.append("     averageWriteTime: ............. ").append(cacheStats.getAverageWriteTime()).append('\n');
                sb.append("     averageRemoveTime: ............. ").append(cacheStats.getAverageRemoveTime()).append('\n');
				/*
				sb.append("     getInMemorySize: ............. ").append(cacheStats.getInMemorySize()).append('\n');
				sb.append("     getOnDiskSize: ............... ").append(cacheStats.getOnDiskSize()).append('\n');
				*/
                sb.append("=================================================\n");

                return sb.toString();
            }

        });

        _commands.add(new TelnetCommand("aistats", "as") {

            @Override
            public String getUsage() {
                return "aistats";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                for (int i = 0; i < AiConfig.AI_TASK_MANAGER_COUNT; i++) {
                    sb.append("AiTaskManager #").append(i + 1).append('\n');
                    sb.append("=================================================\n");
                    sb.append(AiTaskManager.getInstance().getStats(i));
                    sb.append("=================================================\n");
                }

                return sb.toString();
            }

        });
        _commands.add(new TelnetCommand("effectstats", "es") {

            @Override
            public String getUsage() {
                return "effectstats";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ServerConfig.EFFECT_TASK_MANAGER_COUNT; i++) {
                    sb.append("EffectTaskManager #").append(i + 1).append('\n');
                    sb.append("=================================================\n");
                    sb.append(EffectTaskManager.getInstance().getStats(i));
                    sb.append("=================================================\n");
                }

                return sb.toString();
            }

        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}