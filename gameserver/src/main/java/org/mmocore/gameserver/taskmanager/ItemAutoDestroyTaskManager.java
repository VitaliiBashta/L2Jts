package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;

import java.util.concurrent.Future;

/**
 * Менеджер задач для удаления дропнутых итемов или хербов, шаг выполенния задач - неизвестно, ибо зависит от конфига.
 *
 * @author Java-man
 */
public class ItemAutoDestroyTaskManager extends SteppingRunnableQueueManager {
    private static final long DELAY_FOR_ITEMS = ServerConfig.AUTODESTROY_ITEM_AFTER * 1000L;
    private static final long PLAYER_DELAY_FOR_ITEMS = ServerConfig.AUTODESTROY_PLAYER_ITEM_AFTER * 1000L;
    private static final long DELAY_FOR_HERBS = 60000;

    private static final ItemAutoDestroyTaskManager INSTANCE = new ItemAutoDestroyTaskManager();

    private ItemAutoDestroyTaskManager() {
        super(10000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
        //Очистка каждые 60 секунд
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this::purge, 60000L, 60000L);
    }

    public static ItemAutoDestroyTaskManager getInstance() {
        return INSTANCE;
    }

    public Future<?> addAutoDestroyTask(final ItemInstance item, final Creature dropper) {
        final long delay = item.isHerb() ? DELAY_FOR_HERBS : dropper.isPlayer() ? PLAYER_DELAY_FOR_ITEMS : DELAY_FOR_ITEMS;

        return schedule(item::deleteMe, delay);
    }
}
