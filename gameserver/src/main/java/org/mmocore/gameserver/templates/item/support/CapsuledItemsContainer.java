package org.mmocore.gameserver.templates.item.support;

public class CapsuledItemsContainer {
    private final int _itemId;
    private final int _minCount;
    private final int _maxCount;
    private final double _chance;

    public CapsuledItemsContainer(int itemId, int minCount, int maxCount, double chance) {
        this._itemId = itemId;
        this._minCount = minCount;
        this._maxCount = maxCount;
        this._chance = chance;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getMinCount() {
        return _minCount;
    }

    public int getMaxCount() {
        return _maxCount;
    }

    public double getChance() {
        return _chance;
    }
}
