package org.mmocore.gameserver.taskmanager;

import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.taskmanager.tasks.*;
import org.mmocore.gameserver.taskmanager.tasks.custom.SubscriptionGiftTask;
import org.mmocore.gameserver.taskmanager.tasks.custom.SubscriptionResetGiftTask;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 18.08.2016 6:01
 */
public class TaskLoader {
    private static final TaskLoader instance = new TaskLoader();
    private final List<AutomaticTask> tasks;

    private TaskLoader() {
        tasks = new ArrayList<>();
        if (CustomConfig.subscriptionRandomBonus) {
            tasks.add(new SubscriptionGiftTask());
            tasks.add(new SubscriptionResetGiftTask());
        }
        tasks.add(new DeleteExpiredMailTask());
        tasks.add(new DeleteExpiredVarsTask());
        tasks.add(new OlympiadSaveTask());
        tasks.add(new ProductItemCleanUpTask());
        tasks.add(new RecNevitResetTask());
        tasks.add(new ResetReportPoint());
        tasks.add(new SoIStageUpdater());
        tasks.add(new EpicBossAnnounceTask()); //TODO[Hack]: config
    }

    public static TaskLoader getInstance() {
        return instance;
    }

    public void load(Scheduler scheduler) throws SchedulerException {
        scheduler.start();
        tasks.forEach(task -> task.init(scheduler));
    }
}
