package org.mmocore.gameserver.model.reward;

public class RewardItem {
    public final int itemId;
    public long count;
    public boolean isAdena;

    public RewardItem(final int itemId) {
        this.itemId = itemId;
        count = 1;
    }
}
