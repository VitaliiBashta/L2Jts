package org.mmocore.gameserver.scripts.events.NcSoft.JackGame;

import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.taskmanager.tasks.AutomaticTask;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author KilRoy
 */
public class JackGameEventTask extends AutomaticTask implements OnInitScriptListener {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 59 23 * * ?");

    public JackGameEventTask() {
    }

    @Override
    public void doTask() {
        if (JackGame.isActive()) {
            JackGame.changeEventDay();
            logger.info("JackGameDayChange: Task done in change day.");
        }
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(PATTERN)
                .build();
    }

    @Override
    public void onInit() {
        if (JackGame.isActive()) {
            super.init(GameServer.getInstance().getScheduler());
        }
    }
}