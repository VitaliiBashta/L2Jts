package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @author Java-man
 */
public abstract class AutomaticTask extends AbstractGameServerDAO implements Job {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AutomaticTask() {
    }

    protected abstract void doTask();

    public abstract Trigger getTrigger();

    public void init(final Scheduler scheduler) {
        final Class<? extends AutomaticTask> jobClass = getClass();
        final JobDetail job = JobBuilder.newJob(jobClass).build();
        final Trigger trigger = getTrigger();
        try {
            scheduler.scheduleJob(job, trigger);
            logger.info("Task {} was init.", jobClass.getSimpleName());
        } catch (final SchedulerException e) {
            logger.error("Can't schedule job.", e);
        }
    }

    @Override
    public void execute(final JobExecutionContext context) {
        try {
            doTask();
        } catch (final Exception e) {
            logger.error("Can't execute job " + getClass().getSimpleName(), e);
        }
    }
}
