package org.mmocore.gameserver.object.components.items;

/**
 * Абстрактное описание предмета, безопасное для любых операций. Может использоваться как ссылка на уже существующий предмет либо как набор информации для создания нового.
 */
public final class TradeItem extends ItemInfo {
    private long _price;
    private double _price_percent;
    private long _referencePrice;
    private long _currentValue;
    private int _lastRechargeTime;
    private int _rechargeTime;

    public TradeItem() {
        super();
    }

    public TradeItem(ItemInstance item) {
        super(item);
        setReferencePrice(item.getReferencePrice());
    }

    public long getOwnersPrice() {
        return _price;
    }

    public void setOwnersPrice(long price) {
        _price = price;
    }

    public double getPercentPrice() {
        return _price_percent;
    }

    public void setPercentPrice(double percent) {
        _price_percent = percent;
    }

    public long getReferencePrice() {
        return _referencePrice;
    }

    public void setReferencePrice(long price) {
        _referencePrice = price;
    }

    public long getStorePrice() {
        return getReferencePrice() / 2;
    }

    public long getCurrentValue() {
        return _currentValue;
    }

    public void setCurrentValue(long value) {
        _currentValue = value;
    }
}