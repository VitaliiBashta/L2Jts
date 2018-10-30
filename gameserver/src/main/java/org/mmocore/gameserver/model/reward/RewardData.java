package org.mmocore.gameserver.model.reward;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class RewardData implements Cloneable {
    private final int _itemId;
    private boolean _notRate;

    private long _mindrop;
    private long _maxdrop;
    private double _chance;
    private double _chanceInGroup;

    public RewardData(final int itemId) {
        _itemId = itemId;
    }

    public RewardData(final int itemId, final long min, final long max, final double chance) {
        this(itemId);
        _mindrop = min;
        _maxdrop = max;
        _chance = chance;
    }

    /**
     * Рейты к вещи не применяются
     */
    public boolean notRate() {
        return _notRate;
    }

    public void setNotRate(final boolean notRate) {
        _notRate = notRate;
    }

    public int getItemId() {
        return _itemId;
    }

    public ItemTemplate getItem() {
        return ItemTemplateHolder.getInstance().getTemplate(_itemId);
    }

    public long getMinDrop() {
        return _mindrop;
    }

    public void setMinDrop(final long mindrop) {
        _mindrop = mindrop;
    }

    public long getMaxDrop() {
        return _maxdrop;
    }

    public void setMaxDrop(final long maxdrop) {
        _maxdrop = maxdrop;
    }

    public double getChance() {
        return _chance;
    }

    public void setChance(final double chance) {
        _chance = chance;
    }

    public double getChanceInGroup() {
        return _chanceInGroup;
    }

    public void setChanceInGroup(final double chance) {
        _chanceInGroup = chance;
    }

    @Override
    public String toString() {
        return "ItemID: " + getItem() + " Min: " + getMinDrop() + " Max: " + getMaxDrop() + " Chance: " + getChance() / 10000.0 + '%';
    }

    @Override
    public RewardData clone() {
        return new RewardData(getItemId(), getMinDrop(), getMaxDrop(), getChance());
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof RewardData) {
            final RewardData drop = (RewardData) o;
            return drop.getItemId() == getItemId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _itemId;
    }

    /**
     * Подсчет шанса выпадения этой конкретной вещи
     * Используется в эвентах и некоторых специальных механизмах
     *
     * @param player игрок (его бонус влияет на шанс)
     * @param mod    (просто множитель шанса)
     * @return информация о выпавшей вещи
     */
    public List<RewardItem> roll(final Player player, final double mod) {
        double rate = 1.0;
        if (getItem().isAdena())
            rate = ServerConfig.RATE_DROP_ADENA * player.getRateAdena();
        else if (getItem().getItemId() == 9912) // epaulette
            rate = player.getPremiumAccountComponent().getPremiumBonus().getBonusEpaulette();
        else
            rate = ServerConfig.RATE_DROP_ITEMS * (player != null ? player.getRateItems() : 1.);
        return roll(rate * mod);
    }

    /**
     * Подсчет шанса выпадения этой конкретной вещи
     * Используется в эвентах и некоторых специальных механизмах
     *
     * @param rate множитель количества
     * @return информация о выпавшей вещи
     */
    public List<RewardItem> roll(final double rate) {
        final double mult = Math.ceil(rate);

        final List<RewardItem> ret = new ArrayList<>(1);
        RewardItem t = null;
        long count;
        for (int n = 0; n < mult; n++) {
            if (Rnd.get(RewardList.MAX_CHANCE) <= _chance * Math.min(rate - n, 1.0)) {
                if (getMinDrop() >= getMaxDrop()) {
                    count = getMinDrop();
                } else {
                    count = Rnd.get(getMinDrop(), getMaxDrop());
                }

                if (t == null) {
                    ret.add(t = new RewardItem(getItemId()));
                    t.count = count;
                } else {
                    t.count = Math.addExact(t.count, count);
                }
            }
        }

        return ret;
    }
}