package org.mmocore.gameserver.model.buylist;

import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Create by Mangol
 * За идею спасибо l2j
 */
public class Product {
    private int listId;
    private ItemTemplate item;
    private double percent;
    private long price;
    private long restoreDelay;
    private long maxCount;
    private long nextRestoreTime;
    private AtomicLong count;

    public Product() {
    }

    public Product(final int listId, final ItemTemplate item, final double percent, final long restoreDelay, final long maxCount) {
        this.listId = listId;
        this.item = item;
        this.percent = percent;
        this.restoreDelay = restoreDelay;
        this.maxCount = maxCount;
        this.nextRestoreTime = (System.currentTimeMillis() / 60000L) + restoreDelay;
        if (hasLimitedStock()) {
            count = new AtomicLong(maxCount);
        }
    }

    public ItemTemplate getItem() {
        return item;
    }

    public void setItem(final ItemTemplate template) {
        this.item = template;
    }

    public int getItemId() {
        return item.getItemId();
    }

    public int getListId() {
        return listId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public double getPercent() {
        return percent;
    }

    public long getRestoreDelay() {
        return restoreDelay;
    }

    public long getMaxCount() {
        return maxCount;
    }

    public long getNextRestoreTime() {
        return nextRestoreTime;
    }

    public long getCount() {
        if (count == null) {
            return 0;
        }
        if (hasLimitedStock() && isPendingStockUpdate()) {
            restoreMaxCount();
        }
        final long ret = count.get();
        return ret > 0 ? ret : 0;
    }

    public void setCount(final long currentCount) {
        if (count == null) {
            count = new AtomicLong();
        }
        count.set(currentCount);
    }

    public boolean decreaseCount(final long val) {
        return count.addAndGet(-val) >= 0;
    }

    public boolean isPendingStockUpdate() {
        return (System.currentTimeMillis() / 60000L) >= getNextRestoreTime();
    }

    public void restoreMaxCount() {
        if (isPendingStockUpdate() && getRestoreDelay() > 0) {
            setCount(getMaxCount());
            nextRestoreTime = (System.currentTimeMillis() / 60000L) + getRestoreDelay();
        }
    }

    public boolean hasLimitedStock() {
        return getMaxCount() > 0;
    }

    public long generatePrice(final double taxRate) {
        final double price = getItem().getReferencePrice() * (1. + ((getPercent() / 100) + (taxRate / 100)));
        return Math.round(price);
    }
}
