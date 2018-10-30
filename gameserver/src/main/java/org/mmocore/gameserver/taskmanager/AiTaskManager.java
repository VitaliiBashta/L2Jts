package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AiConfig;

/**
 * Менеджер задач AI, шаг выполенния задач 250 мс.
 *
 * @author G1ta0
 */
public class AiTaskManager extends SteppingRunnableQueueManager {
    private static final long TICK = 250L;

    private static final AiTaskManager[] _instances = new AiTaskManager[AiConfig.AI_TASK_MANAGER_COUNT];
    private static int randomizer = 0;

    static {
        for (int i = 0; i < _instances.length; i++) {
            _instances[i] = new AiTaskManager();
        }
    }

    private AiTaskManager() {
        super(TICK);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(TICK), TICK);
        //Очистка каждую минуту со сдвигом
        ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable) () -> {
            purge();
            if (randomizer > 10000000)
                randomizer = 0;
        }, 60000L + 1000 * randomizer++, 60000L);
    }

    public static AiTaskManager getInstance() {
        return _instances[randomizer++ % _instances.length];
    }

    public CharSequence getStats(final int num) {
        return _instances[num].getStats();
    }
}
