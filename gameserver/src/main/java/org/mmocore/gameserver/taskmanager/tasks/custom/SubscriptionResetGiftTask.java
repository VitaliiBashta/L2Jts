package org.mmocore.gameserver.taskmanager.tasks.custom;

import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.taskmanager.tasks.AutomaticTask;
import org.mmocore.gameserver.utils.Strings;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Mangol
 * @since 01.05.2016
 */
public class SubscriptionResetGiftTask extends AutomaticTask {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 0 0 * * ?");
    private static final String DELETE_EXPIRED_VARIABLES = "DELETE FROM character_variables WHERE obj_id=? AND type='player-var' AND name=? LIMIT 1";
    private static final String SELECT_EXPIRED_VARIABLE = "SELECT obj_id, name FROM character_variables WHERE name ='SUBSCRIPTION_DAY_GIFT'";

    @Override
    protected void doTask() throws Exception {
        if (!CustomConfig.subscriptionAllow) {
            return;
        }
        final Map<Integer, PlayerVariables> varMap = new HashMap<>();
        try {
            jdbcHelper.query(SELECT_EXPIRED_VARIABLE, new ResultSetHandler() {
                @Override
                public void processRow(final ResultSet rs) throws SQLException {
                    final PlayerVariables name = PlayerVariables.valueOf(rs.getString("name"));
                    final String objId = Strings.stripSlashes(rs.getString("obj_id"));
                    varMap.put(Integer.parseInt(objId), name);
                }
            });
        } catch (final Exception e) {
            logger.error("", e);
        }

        if (!varMap.isEmpty()) {
            for (final Map.Entry<Integer, PlayerVariables> entry : varMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    logger.error("Null somewhere: {}, {}", entry.getKey(), entry.getValue());
                    continue;
                }
                final Player player = GameObjectsStorage.getPlayer(entry.getKey());
                if (player != null && player.isOnline() && !player.isGM()) {
                    player.getPlayerVariables().remove(entry.getValue());
                    final CustomPlayerComponent component = player.getCustomPlayerComponent();
                    component.saveSubscriptionTimeTask(false);
                    component.stopSubscriptionTask();
                    component.addTimeSubscription(TimeUnit.HOURS.toMillis(CustomConfig.subscriptionRandomHourAdd));
                    if (!player.isInZone(ZoneType.peace_zone)) {
                        component.startSubscription();
                    }
                } else {
                    jdbcHelper.execute(DELETE_EXPIRED_VARIABLES, entry.getKey(), entry.getValue().name());
                }
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
}
