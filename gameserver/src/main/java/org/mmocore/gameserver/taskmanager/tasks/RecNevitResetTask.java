package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author VISTALL
 * @date 5:56/12.08.2011
 */
public class RecNevitResetTask extends AutomaticTask {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 30 6 * * ?");

    public RecNevitResetTask() {
    }

    @Override
    public void doTask() {
        final long t = System.currentTimeMillis();
        logger.info("RecNevitResetTask: start.");
        for (final Player player : GameObjectsStorage.getPlayers()) {
            player.getRecommendationComponent().restartRecom();
            player.getNevitComponent().restartSystem();
        }
        logger.info("RecNevitResetTask: done in " + (System.currentTimeMillis() - t) + " ms.");
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(PATTERN)
                .build();
    }
}
