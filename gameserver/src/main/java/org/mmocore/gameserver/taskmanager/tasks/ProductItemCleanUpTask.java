package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author KilRoy
 */
public class ProductItemCleanUpTask extends AutomaticTask {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 0 13 * * ?");

    public ProductItemCleanUpTask() {
    }

    @Override
    public void doTask() {
        ProductItemHolder.getInstance().clearBoughtProducts();
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(PATTERN)
                .build();
    }
}
