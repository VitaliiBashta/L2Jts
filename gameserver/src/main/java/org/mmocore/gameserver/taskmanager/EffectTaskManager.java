package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;

/**
 * Менеджер задач для работы с эффектами, шаг выполенния задач 250 мс.
 *
 * @author G1ta0
 */
public class EffectTaskManager extends SteppingRunnableQueueManager {
    private static final long TICK = 250L;

    private static final EffectTaskManager[] _instances = new EffectTaskManager[ServerConfig.EFFECT_TASK_MANAGER_COUNT];
    private static int randomizer;

    static {
        for (int i = 0; i < _instances.length; i++) {
            _instances[i] = new EffectTaskManager();
        }
    }

    private EffectTaskManager() {
        super(TICK);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(TICK), TICK);
        //Очистка каждые 30 секунд со сдвигом
        ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable) () -> {
            purge();
            if (randomizer > 10000000)
                randomizer = 0;

        }, 30000L + 1000 * randomizer++, 30000L);
    }

    public static EffectTaskManager getInstance() {
        return _instances[randomizer++ % _instances.length];
    }

    public CharSequence getStats(final int num) {
        return _instances[num].getStats();
    }
}