package org.mmocore.gameserver.templates.custom;

/**
 * Created by Hack
 * Date: 05.09.2016 1:56
 */
public class CastleRewardTemplate {
    private int itemId;
    private int minCount;
    private int maxCount;
    private double chance;

    public CastleRewardTemplate(int itemId, int minCount, int maxCount, double chance) {
        this.itemId = itemId;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.chance = chance;
    }

    public int getItemId() {
        return itemId;
    }

    public int getMinCount() {
        return minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public double getChance() {
        return chance;
    }
}
