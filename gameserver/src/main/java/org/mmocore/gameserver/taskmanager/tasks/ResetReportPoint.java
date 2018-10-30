package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.ResetAllPlayerReportPoints;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author KilRoy
 */
public class ResetReportPoint extends AutomaticTask {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 30 6 * * ?");

    public ResetReportPoint() {
    }

    @Override
    public void doTask() throws Exception {
        final long t = System.currentTimeMillis();
        logger.info("ResetReportPoint: start.");
        for (final Player player : GameObjectsStorage.getPlayers())
            player.getBotPunishComponent().requestReportsPoints(true, 10);
        AuthServerCommunication.getInstance().sendPacket(new ResetAllPlayerReportPoints(ExtConfig.DEFAULT_COUNT_REPORT_POINTS));
        logger.info("ResetReportPoint: done in " + (System.currentTimeMillis() - t) + " ms.");
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(PATTERN)
                .build();
    }
}