package org.mmocore.gameserver.templates;

/**
 * Created by Hack
 * Date: 06.06.2016 22:13
 */
public class QuestCustomParams {
    private final int id;
    private final int levelMin;
    private final int levelMax;

    public QuestCustomParams(int id, int levelMin, int levelMax) {
        this.id = id;
        this.levelMin = levelMin;
        this.levelMax = levelMax;
    }

    public int getId() {
        return id;
    }

    public int getLevelMax() {
        return levelMax;
    }

    public int getLevelMin() {
        return levelMin;
    }
}
