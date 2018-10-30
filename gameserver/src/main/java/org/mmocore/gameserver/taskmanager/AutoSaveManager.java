package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.object.Player;

import java.util.concurrent.Future;

/**
 * Менеджер автосохранения игроков, шаг выполенния задач 10с.
 *
 * @author G1ta0
 */
public class AutoSaveManager extends SteppingRunnableQueueManager {
    private AutoSaveManager() {
        super(10000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
        //Очистка каждые 60 секунд
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this::purge, 60000L, 60000L);
    }

    public static AutoSaveManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Future<?> addAutoSaveTask(final Player player) {
        final long delay = Rnd.get(180, 360) * 1000L;

        return scheduleAtFixedRate((Runnable) () ->
        {
            if (!player.isOnline())
                return;

            player.store(true);

        }, delay, delay);
    }

    private static class LazyHolder {
        private static final AutoSaveManager INSTANCE = new AutoSaveManager();
    }
}