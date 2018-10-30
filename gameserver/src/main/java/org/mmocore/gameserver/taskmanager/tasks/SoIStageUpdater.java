package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.manager.SoIManager;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author VISTALL
 * @date 5:53/12.08.2011
 */
public class SoIStageUpdater extends AutomaticTask {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 0 12 ? * mon");

    public SoIStageUpdater() {
    }

    @Override
    public void doTask() {
        SoIManager.setCurrentStage(1);

        logger.info("Seed of Infinity update Task: Seed updated successfuly.");
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(PATTERN)
                .build();
    }
}
