package org.mmocore.gameserver.templates.custom;

/**
 * Created by Hack
 * Date: 09.10.2016 6:47
 */
public class HeroRewardTemplate {
    private int itemId;
    private int itemCount;
    private double chance;

    public HeroRewardTemplate(int itemId, int itemCount, double chance) {
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.chance = chance;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public double getChance() {
        return chance;
    }
}
