package org.mmocore.gameserver;

import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.CoupleManager;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.manager.games.FishingChampionShipManager;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadDatabase;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс отвечает за запланированное отключение сервера и связанные с этим мероприятия.
 *
 * @author G1ta0
 */
public class Shutdown extends Thread {
    public static final int SHUTDOWN = 0;
    public static final int RESTART = 2;
    public static final int NONE = -1;
    private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);
    private static final Shutdown _instance = new Shutdown();
    private Timer counter;
    private int shutdownMode;
    private int shutdownCounter;
    private Shutdown() {
        setName(getClass().getSimpleName());
        setDaemon(true);

        shutdownMode = NONE;
    }

    public static Shutdown getInstance() {
        return _instance;
    }

    /**
     * Время в секундах до отключения.
     *
     * @return время в секундах до отключения сервера, -1 если отключение не запланировано
     */
    public int getSeconds() {
        return shutdownMode == NONE ? -1 : shutdownCounter;
    }

    /**
     * Режим отключения.
     *
     * @return <code>SHUTDOWN</code> или <code>RESTART</code>, либо <code>NONE</code>, если отключение не запланировано.
     */
    public int getMode() {
        return shutdownMode;
    }

    /**
     * Запланировать отключение сервера через определенный промежуток времени.
     * <p>
     * время в формате <code>hh:mm</code>
     *
     * @param shutdownMode <code>SHUTDOWN</code> или <code>RESTART</code>
     */
    public synchronized void schedule(final int seconds, final int shutdownMode) {
        if (seconds < 0) {
            return;
        }

        if (counter != null) {
            counter.cancel();
        }

        this.shutdownMode = shutdownMode;
        this.shutdownCounter = seconds;

        _log.info("Scheduled server " + (shutdownMode == SHUTDOWN ? "shutdown" : "restart") + " in " + Util.formatTime(seconds) + '.');

        counter = new Timer("ShutdownCounter", true);
        counter.scheduleAtFixedRate(new ShutdownCounter(), 0, 1000L);
    }

    /**
     * Запланировать отключение сервера на определенное время.
     *
     * @param time         время в формате cron
     * @param shutdownMode <code>SHUTDOWN</code> или <code>RESTART</code>
     */
    public void schedule(final String time, final int shutdownMode) {
        final CronExpression cronTime = QuartzUtils.createCronExpression(time);
        if (cronTime != null) {
            final int seconds = (int) (cronTime.getNextValidTimeAfter(new Date()).getTime() / 1000L - System.currentTimeMillis() / 1000L);
            schedule(seconds, shutdownMode);
        }
    }

    /**
     * Отменить запланированное отключение сервера.
     */
    public synchronized void cancel() {
        shutdownMode = NONE;
        if (counter != null) {
            counter.cancel();
        }
        counter = null;
    }

    @Override
    public void run() {
        System.out.println("Shutting down LS/GS communication...");
        AuthServerCommunication.getInstance().shutdown();

        System.out.println("Disconnecting players...");
        disconnectAllPlayers();

        System.out.println("Saving data...");
        saveData();

        try {
            GameServer.getInstance().getScheduler().shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Shutting down thread pool...");
            ThreadPoolManager.getInstance().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Shutting down selector...");
        if (GameServer.getInstance() != null) {
            try {
                GameServer.getInstance().getSelectorThreads().shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // stop caches
        HtmCache.getInstance().doStop();
        GameServer.getInstance().getCacheManager().stop();

        try {
            System.out.println("Shutting down database communication...");
            DatabaseFactory.getInstance().doStop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Shutdown finished.");
    }

    private void saveData() {
        if (GameServer.getInstance().getTelnetServer() != null)
            GameServer.getInstance().getTelnetServer().shutdown();

        try {
            // Seven Signs data is now saved along with Festival data.
            if (!SevenSigns.getInstance().isSealValidationPeriod()) {
                SevenSignsFestival.getInstance().saveFestivalData(false);
                System.out.println("SevenSignsFestival: Data saved.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SevenSigns.getInstance().saveSevenSignsData(0, true);
            System.out.println("SevenSigns: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (OlympiadConfig.ENABLE_OLYMPIAD) {
            try {
                OlympiadDatabase.save();
                System.out.println("Olympiad: Data saved.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ServerConfig.ALLOW_WEDDING) {
            try {
                CoupleManager.getInstance().store();
                System.out.println("CoupleManager: Data saved.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FishingChampionShipManager.getInstance().shutdown();
            System.out.println("FishingChampionShipManager: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Hero.getInstance().shutdown();
            System.out.println("Hero: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ServerConfig.ALLOW_CURSED_WEAPONS) {
            try {
                CursedWeaponsManager.getInstance().saveData();
                System.out.println("CursedWeaponsManager: Data saved,");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnectAllPlayers() {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            try {
                player.logout();
            } catch (Exception e) {
                System.out.println("Error while disconnecting: " + player + '!');
                e.printStackTrace();
            }
        }
    }

    private class ShutdownCounter extends TimerTask {
        @Override
        public void run() {
            switch (shutdownCounter) {
                case 1800:
                case 900:
                case 600:
                case 300:
                case 240:
                case 180:
                case 120:
                case 60:
                    if (ServerConfig.customShutdownMessages) {
                        AnnouncementUtils.announceMultilang("Сервер будет отключен через " + (shutdownCounter / 60) + " минут.",
                                "The server will be coming down in " + (shutdownCounter / 60) + " minutes.");
                    } else {
                        AnnouncementUtils.announceToAll(new CustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES")
                                .addNumber(shutdownCounter / 60).toString());
                    }
                    break;
                case 30:
                case 20:
                case 10:
                case 5:
                    if (ServerConfig.customShutdownMessages) {
                        AnnouncementUtils.announceWithSplash("Сервер будет отключен через " + shutdownCounter + " секунд.",
                                "The server will be coming down in " + (shutdownCounter) + " seconds.");
                    } else {
                        AnnouncementUtils.announceToAll(new SystemMessage(SystemMsg.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT)
                                .addNumber(shutdownCounter));
                    }
                    break;
                case 0:
                    switch (shutdownMode) {
                        case SHUTDOWN:
                            Runtime.getRuntime().exit(SHUTDOWN);
                            break;
                        case RESTART:
                            Runtime.getRuntime().exit(RESTART);
                            break;
                    }
                    cancel();
                    return;
            }

            shutdownCounter--;
        }
    }
}
