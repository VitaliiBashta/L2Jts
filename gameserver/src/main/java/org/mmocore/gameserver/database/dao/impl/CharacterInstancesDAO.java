package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

/**
 * @author Java-man
 */
public class CharacterInstancesDAO extends AbstractGameServerDAO {
    public static CharacterInstancesDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Map<Integer, Long> loadInstanceReuses(final int objectId) {
        return jdbcHelper.queryForMap("SELECT * FROM character_instances WHERE obj_id = ?",
                rs -> new SimpleEntry<>(rs.getInt("id"), rs.getLong("reuse")), objectId);
    }

    public void setInstanceReenterTime(final int objectId, final int instancedZoneId, final long time) {
        jdbcHelper.execute("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", objectId, instancedZoneId, time);
    }

    public void setInstanceReuse(final int objectId, final int instanceId, final long time) {
        jdbcHelper.execute("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", objectId, instanceId, time);
    }

    public void removeInstanceReuse(final int objectId, final int instanceId) {
        jdbcHelper.execute("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", objectId, instanceId);
    }

    public void removeAllInstanceReuses(final int objectId) {
        jdbcHelper.execute("DELETE FROM `character_instances` WHERE `obj_id`=?", objectId);
    }

    private static class LazyHolder {
        private static final CharacterInstancesDAO INSTANCE = new CharacterInstancesDAO();
    }
}
