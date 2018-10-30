package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.gameserver.ThreadPoolManager;

/**
 * Менеджер регенерации HP/MP/CP персонажей, шаг выполенния задач 1с.
 *
 * @author G1ta0
 */
public class RegenTaskManager extends SteppingRunnableQueueManager {
    private RegenTaskManager() {
        super(1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
        //Очистка каждые 10 секунд
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this::purge, 10000L, 10000L);
    }

    public static RegenTaskManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final RegenTaskManager INSTANCE = new RegenTaskManager();
    }
}