package org.mmocore.gameserver.scripts.events.NcSoft.EvasInferno;

import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.taskmanager.tasks.AutomaticTask;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author KilRoy
 */
public class EvasInfernoEventTask extends AutomaticTask implements OnInitScriptListener {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 59 23 * * ?");

    public EvasInfernoEventTask() {
    }

    @Override
    public void doTask() throws Exception {
        if (EvasInferno.isActive()) {
            final int nextStage = EvasInferno.getCurrentEventStage().ordinal() + 1;
            if (nextStage < 14) {
                EvasInferno.setEventElapsedTime(EvasInferno.getEventElapsedTime() + 86400);
                EvasInferno.startNewStage(nextStage);
                logger.info("EvasInfernoStageUpdate: Task done in change stage. New stage " + EvasInferno.getCurrentEventStage().ordinal());
            }
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
        if (EvasInferno.isActive()) {
            super.init(GameServer.getInstance().getScheduler());
        }
    }
}