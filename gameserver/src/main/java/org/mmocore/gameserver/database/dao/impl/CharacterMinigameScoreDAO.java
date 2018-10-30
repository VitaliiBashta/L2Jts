package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.games.MiniGameScoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author VISTALL
 * @date 1:16/21.08.2011
 */
public class CharacterMinigameScoreDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterMinigameScoreDAO.class);
    private static final CharacterMinigameScoreDAO _instance = new CharacterMinigameScoreDAO();

    public static CharacterMinigameScoreDAO getInstance() {
        return _instance;
    }

    public void select() {
        Connection con = null;
        Statement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            rset = statement.executeQuery("SELECT characters.char_name AS name, character_minigame_score.score AS score, character_minigame_score.object_id AS object_id FROM characters, character_minigame_score WHERE characters.obj_Id=character_minigame_score.object_id");
            while (rset.next()) {
                final String name = rset.getString("name");
                final int score = rset.getInt("score");
                final int objectId = rset.getInt("object_id");

                MiniGameScoreManager.getInstance().addScore(objectId, score, name);
            }
        } catch (SQLException e) {
            _log.info("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void replace(final int objectId, final int score) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO character_minigame_score(object_id, score) VALUES (?, ?)");
            statement.setInt(1, objectId);
            statement.setInt(2, score);
            statement.execute();
        } catch (final Exception e) {
            _log.info("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
