package org.mmocore.gameserver.templates.client;

/**
 * Create by Mangol on 21.12.2015.
 */
public class QuestLine {
    private final int questId;
    private final String questName;
    private final int minLevel;
    private final int maxLevel;
    private final String reward;

    public QuestLine(final int questId, final String questName, final int minLevel, final int maxLevel, final String reward) {
        this.questId = questId;
        this.questName = questName;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.reward = reward;
    }

    public int getTestQuestId() {
        return questId;
    }

    public String getTestNameId() {
        return questName;
    }

    public int getTestMinLevel() {
        return minLevel;
    }

    public int getTestMaxLevel() {
        return maxLevel;
    }

    public String getReward() {
        return reward;
    }
}
