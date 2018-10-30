package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.manager.games.MiniGameScoreManager;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

/**
 * @author VISTALL
 * @date 0:07:05/10.04.2010
 */
public class ExBR_MiniGameLoadScores extends GameServerPacket {
    private final List<MiniGameScoreManager.MiniGameScore> entries;
    private int place;
    private int score;
    private int lastScore;

    public ExBR_MiniGameLoadScores(final Player player) {
        int i = 1;

        final NavigableSet<MiniGameScoreManager.MiniGameScore> score = MiniGameScoreManager.getInstance().getScores();
        entries = new ArrayList<>(score.size() >= 100 ? 100 : score.size());

        final MiniGameScoreManager.MiniGameScore last = score.isEmpty() ? null : score.last();
        if (last != null) {
            lastScore = last.getScore();
        }

        for (final MiniGameScoreManager.MiniGameScore entry : score) {
            if (i > 100) {
                break;
            }

            if (entry.getObjectId() == player.getObjectId()) {
                place = i;
                this.score = entry.getScore();
            }
            entries.add(entry);
            i++;
        }
    }

    @Override
    protected void writeData() {
        writeD(place); // place of last big score of player
        writeD(score); // last big score of player
        writeD(entries.size()); //
        writeD(lastScore); //last score of list
        for (int i = 0; i < entries.size(); i++) {
            final MiniGameScoreManager.MiniGameScore pair = entries.get(i);
            writeD(i + 1);
            writeS(pair.getName());
            writeD(pair.getScore());
        }
    }
}