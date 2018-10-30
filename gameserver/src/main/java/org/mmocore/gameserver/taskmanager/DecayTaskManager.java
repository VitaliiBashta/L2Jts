package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.object.Creature;

import java.util.concurrent.Future;

/**
 * Менеджер задач по "исчезновению" убитых персонажей, шаг выполенния задач 500 мс.
 *
 * @author G1ta0
 */
public class DecayTaskManager extends SteppingRunnableQueueManager {
    private static final DecayTaskManager _instance = new DecayTaskManager();

    private DecayTaskManager() {
        super(500L);

        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 500L, 500L);

        //Очистка каждую минуту
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this::purge, 60000L, 60000L);
    }

    public static DecayTaskManager getInstance() {
        return _instance;
    }

    public Future<?> addDecayTask(final Creature actor, final long delay) {
        return schedule(actor::doDecay, delay);
    }
}