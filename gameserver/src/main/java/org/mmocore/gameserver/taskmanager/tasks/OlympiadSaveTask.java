package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadDatabase;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;

/**
 * @author VISTALL
 * @date 20:11/24.06.2011
 */
public class OlympiadSaveTask extends AutomaticTask {
    public OlympiadSaveTask() {
    }

    @Override
    public void init(final Scheduler scheduler) {
        if (!OlympiadConfig.ENABLE_OLYMPIAD)
            return;

        super.init(scheduler);
    }

    @Override
    public void doTask() {
        final long t = System.currentTimeMillis();

        logger.info("OlympiadSaveTask: data save started.");
        OlympiadDatabase.save();
        logger.info("OlympiadSaveTask: data save ended in time: " + (System.currentTimeMillis() - t) + " ms.");
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(repeatMinutelyForever(1000))
                .build();
    }
}
