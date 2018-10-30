package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Strings;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;

/**
 * @author pchayka
 */
public class DeleteExpiredVarsTask extends AutomaticTask {
    private static final String DELETE_EXPIRED_VARIABLES = "DELETE FROM character_variables WHERE obj_id=? AND type='player-var' AND name=? LIMIT 1";
    private static final String SELECT_EXPIRED_VARIABLE = "SELECT obj_id, name FROM character_variables WHERE expire_time > 0 AND expire_time < ?";

    @Override
    public void doTask() {
        final Map<Integer, PlayerVariables> varMap = new HashMap<>();
        try {
            jdbcHelper.query(SELECT_EXPIRED_VARIABLE, new ResultSetHandler() {
                @Override
                public void processRow(final ResultSet rs) throws SQLException {
                    final String name = rs.getString("name");
                    final String objId = Strings.stripSlashes(rs.getString("obj_id"));
                    varMap.put(Integer.parseInt(objId), PlayerVariables.valueOf(name));
                }
            }, System.currentTimeMillis());
        } catch (final Exception e) {
            logger.error("", e);
        }

        if (!varMap.isEmpty()) {
            for (final Entry<Integer, PlayerVariables> entry : varMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    logger.error("Null somewhere: {}, {}", entry.getKey(), entry.getValue());
                    continue;
                }
                final Player player = GameObjectsStorage.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    player.getPlayerVariables().remove(entry.getValue());
                } else {
                    jdbcHelper.execute(DELETE_EXPIRED_VARIABLES, entry.getKey(), entry.getValue().name());
                }
            }
        }
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger().startNow().withSchedule(repeatMinutelyForever(2)).build();
    }
}
