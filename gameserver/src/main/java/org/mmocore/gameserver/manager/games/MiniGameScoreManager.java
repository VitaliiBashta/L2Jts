package org.mmocore.gameserver.manager.games;

import org.mmocore.gameserver.database.dao.impl.CharacterMinigameScoreDAO;
import org.mmocore.gameserver.object.Player;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author VISTALL
 * @date 15:15/15.10.2010
 * @see ext.properties
 */
public class MiniGameScoreManager {
    private final NavigableSet<MiniGameScore> _scores = new ConcurrentSkipListSet<>(new Comparator<MiniGameScore>() {
        @Override
        public int compare(final MiniGameScore o1, final MiniGameScore o2) {
            return o2.getScore() - o1.getScore();
        }
    });

    private MiniGameScoreManager() {
    }

    public static MiniGameScoreManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addScore(final Player player, final int score) {
        MiniGameScore miniGameScore = null;
        for (final MiniGameScore $miniGameScore : _scores) {
            if ($miniGameScore.getObjectId() == player.getObjectId()) {
                miniGameScore = $miniGameScore;
            }
        }

        if (miniGameScore == null) {
            _scores.add(new MiniGameScore(player.getObjectId(), player.getName(), score));
        } else {
            // текущий меньше тот которого есть
            if (miniGameScore.getScore() > score) {
                return;
            }

            miniGameScore.setScore(score);
        }

        CharacterMinigameScoreDAO.getInstance().replace(player.getObjectId(), score);
    }

    public void addScore(final int objectId, final int score, final String name) {
        _scores.add(new MiniGameScore(objectId, name, score));
    }

    public NavigableSet<MiniGameScore> getScores() {
        return _scores;
    }

    public static class MiniGameScore {
        private final int _objectId;
        private final String _name;
        private int _score;

        public MiniGameScore(final int objectId, final String name, final int score) {
            _objectId = objectId;
            _name = name;
            _score = score;
        }

        public int getObjectId() {
            return _objectId;
        }

        public String getName() {
            return _name;
        }

        public int getScore() {
            return _score;
        }

        public void setScore(final int score) {
            _score = score;
        }

        @Override
        public boolean equals(final Object o) {
            return !(o == null || o.getClass() != MiniGameScore.class) && ((MiniGameScore) o).getObjectId() == getObjectId();
        }

        @Override
        public int hashCode() {
            return _objectId;
        }
    }

    private static class LazyHolder {
        private static final MiniGameScoreManager INSTANCE = new MiniGameScoreManager();
    }
}
