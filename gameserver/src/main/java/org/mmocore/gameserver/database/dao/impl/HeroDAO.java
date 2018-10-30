package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;

/**
 * @author Java-man
 */
public class HeroDAO extends AbstractGameServerDAO {
    public static HeroDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void clearHeroes() {
        jdbcHelper.execute("UPDATE heroes SET played = 0, active = 0");
    }

    private static class LazyHolder {
        private static final HeroDAO INSTANCE = new HeroDAO();
    }
}
