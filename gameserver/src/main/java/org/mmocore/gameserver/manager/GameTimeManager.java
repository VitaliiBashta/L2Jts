package org.mmocore.gameserver.manager;

import org.mmocore.commons.listener.ListenerList;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.GameListener;
import org.mmocore.gameserver.listener.game.OnDayNightChangeListener;
import org.mmocore.gameserver.listener.game.OnStartListener;
import org.mmocore.gameserver.network.lineage.serverpackets.ClientSetTime;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Менеджер игрового времени.
 * 1 игровой час = 10 минутам реального времени.
 * 1 игровая минута = 10 секундам реального времени.
 * <p>
 * Пакет ClientSetTime отправляется каждый игровой час.
 *
 * @author Java-man
 */
public class GameTimeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameTimeManager.class);
    private static final int DATETIME_SUNRISE = 6; // час, в который встает солнце
    private static final int DATETIME_SUNSET = 24; // час, в который садится солнце
    private final GameTimeListenerList listenerEngine = new GameTimeListenerList();
    private final Calendar calendar = new GregorianCalendar();
    private final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

    private GameTimeManager() {
        final Calendar cal = loadData();

        if (cal != null) {
            calendar.setTimeInMillis(cal.getTimeInMillis());
        } else {
            calendar.set(Calendar.YEAR, 1281);
            calendar.set(Calendar.MONTH, 5);
            calendar.set(Calendar.DAY_OF_MONTH, 5);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 45);

            saveData();
        }

        GameServer.getInstance().addListener(new OnStartListenerImpl());

        LOGGER.info("Initialized. Current time is {}.", getFormattedGameTime());
    }

    public static GameTimeManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private Calendar loadData() {
        try {
            final String time = ServerVariables.getString("SERVER_TIME", null);

            if (time == null)
                return null;

            final Calendar cal = Calendar.getInstance();

            cal.setTimeInMillis(Long.parseLong(time));

            return cal;
        } catch (Exception e) {
            LOGGER.warn("", e);

            return null;
        }
    }

    private void saveData() {
        ServerVariables.set("SERVER_TIME", calendar.getTimeInMillis());
    }

    private String getFormattedGameTime() {
        return formatter.format(calendar.getTime());
    }

    public int getGameTime() {
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    public int getGameHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getGameMin() {
        return calendar.get(Calendar.MINUTE);
    }

    public boolean isNowNight() {
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour < DATETIME_SUNRISE || hour >= DATETIME_SUNSET;
    }

    public <T extends GameListener> boolean addListener(final T listener) {
        return listenerEngine.add(listener);
    }

    public <T extends GameListener> boolean removeListener(final T listener) {
        return listenerEngine.remove(listener);
    }

    protected static class GameTimeListenerList extends ListenerList<GameServer> {
        public void onDay() {
            for (final OnDayNightChangeListener listener : getListeners(OnDayNightChangeListener.class))
                listener.onDay();
        }

        public void onNight() {
            for (final OnDayNightChangeListener listener : getListeners(OnDayNightChangeListener.class))
                listener.onNight();
        }
    }

    private static class LazyHolder {
        private static final GameTimeManager INSTANCE = new GameTimeManager();
    }

    private class OnStartListenerImpl implements OnStartListener {
        @Override
        public void onStart() {
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new MinuteCounter(), 0, 10000);
        }
    }

    private final class MinuteCounter implements Runnable {
        @Override
        public void run() {
            final boolean isNight = isNowNight();

            final int oldHour = calendar.get(Calendar.HOUR_OF_DAY);
            final int oldDay = calendar.get(Calendar.DAY_OF_YEAR);

            calendar.add(Calendar.MINUTE, 1);

            final int newHour = calendar.get(Calendar.HOUR_OF_DAY);

            // check if one hour passed
            if (oldHour != newHour) {
                // update time for all players
                GameObjectsStorage.getPlayers().stream().forEach(player -> player.sendPacket(ClientSetTime.STATIC));

                // check if night state changed
                if (isNight != isNowNight()) {
                    if (isNowNight()) {
                        listenerEngine.onNight();
                    } else {
                        listenerEngine.onDay();
                    }
                    GameObjectsStorage.getPlayers().stream().forEach(player -> player.checkDayNightMessages());
                }

                final int newDay = calendar.get(Calendar.DAY_OF_YEAR);

                // check if a whole day passed
                if (oldDay != newDay)
                    LOGGER.info("An in-game day passed - it's now: {}", getFormattedGameTime());

                saveData();
            }
        }
    }
}