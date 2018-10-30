package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Java-man
 */
public class CharacterVariablesDAO extends AbstractGameServerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterVariablesDAO.class);
    private static final String SELECT = "SELECT `value` FROM character_variables WHERE `obj_id`=? AND `name` = '" + PlayerVariables.PENALTY_CHAT_COUNT + '\'';
    private static final String REPLACE = "REPLACE INTO character_variables (obj_id, type, name, value, expire_time)" + "VALUES (?,'player-var','" + PlayerVariables.PENALTY_CHAT_COUNT + "','?',-1)";
    private static final CharacterVariablesDAO INSTANCE = new CharacterVariablesDAO();

    public static CharacterVariablesDAO getInstance() {
        return INSTANCE;
    }

    public int selectPenaltyChatCount(final int objectId) {
        return jdbcHelper.queryForInt(SELECT, objectId);
    }

    public void updatePenaltyChatCount(final int objectId, final int count) {
        jdbcHelper.execute(REPLACE, objectId, count);
    }

    public boolean changeClanWarehousePrivileges(final int unitMemberObjectId) {
        try {
            jdbcHelper.holdConnection();

            final int state = jdbcHelper.queryForInt("SELECT value FROM character_variables WHERE obj_id=? AND name LIKE '?' LIMIT 1", unitMemberObjectId, PlayerVariables.CAN_WAREHOUSE_WITHDRAW);
            if (state > 0) {
                jdbcHelper.execute("DELETE FROM `character_variables` WHERE obj_id=? AND name LIKE '?' LIMIT 1", unitMemberObjectId, PlayerVariables.CAN_WAREHOUSE_WITHDRAW);
                return false;
            } else {
                jdbcHelper.execute("INSERT INTO character_variables  (obj_id, type, name, value, expire_time) VALUES (?,'player-var','?','1',-1)", unitMemberObjectId, PlayerVariables.CAN_WAREHOUSE_WITHDRAW);
                return true;
            }
        } finally {
            jdbcHelper.releaseConnection();
        }
    }

    public List<Integer> selectPlayersWithSpecifiedVariable(final IntStream objectIds, final PlayerVariables playerVariable) {
        return jdbcHelper.queryForIntegerList("SELECT `obj_id` FROM `character_variables` WHERE `obj_id` IN (?) AND `name`='?'", objectIds.mapToObj(String::valueOf).collect(Collectors.joining(",")), playerVariable);
    }
}
