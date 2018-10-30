package org.mmocore.authserver.database.dao.impl;

import org.mmocore.authserver.database.dao.AbstractAuthServerDAO;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class GameServerDAO extends AbstractAuthServerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerDAO.class);
    private static final GameServerDAO INSTANCE = new GameServerDAO();

    private static final String SQL_QUERY_SELECT_SERVER = "SELECT server_id FROM gameservers";

    public static GameServerDAO getInstance() {
        return INSTANCE;
    }

    public Set<GameServer> load() {
        final Set<GameServer> gameServers = new LinkedHashSet<>();

        jdbcHelper.query(SQL_QUERY_SELECT_SERVER, new ResultSetHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {

                final int id = rs.getInt("server_id");
                final GameServer gs = new GameServer();
                final GameServer.Entry entry = new GameServer.Entry(id);
                gs.entries.put(id, entry);
                gameServers.add(gs);
            }
        });
        return gameServers;
    }
}