package org.mmocore.gameserver.templates.item;

import org.mmocore.gameserver.object.components.items.premium.ProductItemComponent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @author KilRoy
 * @author Java-man
 */
public class ProductItemTemplate {
    public static final int DEFAULT_MAX_STOCK = -1;
    public static final ZonedDateTime DEFAULT_START_SALE_DATE = ZonedDateTime.of(LocalDateTime.of(1980, 01, 01, 00, 00), ZoneId.systemDefault());
    public static final ZonedDateTime DEFAULT_END_SALE_DATE = ZonedDateTime.of(LocalDateTime.of(2037, 06, 01, 23, 59), ZoneId.systemDefault());
    public static final int DEFAULT_CURRENT_STOCK = 0;
    public static final boolean DEFAULT_IS_EVENT = false;
    private final int productId;
    private final int category;
    private final int price;
    private final int maxStock;
    private final int dayWeek;
    private final ZonedDateTime startSaleDate;
    private final ZonedDateTime endSaleDate;
    private final List<ProductItemComponent> components = new ArrayList<>(1);
    private EventFlag eventsFlag;
    private int buyCount;

    public ProductItemTemplate(final int productId, final int category, final int price, final int dayWeek,
                               final ZonedDateTime startSaleDate, final ZonedDateTime endSaleDate,
                               final int maxStock, final EventFlag eventsFlag) {
        this.productId = productId;
        this.category = category;
        this.price = price;
        this.dayWeek = dayWeek;
        this.maxStock = maxStock;
        this.eventsFlag = eventsFlag;
        this.startSaleDate = startSaleDate;
        this.endSaleDate = endSaleDate;
    }

    public int getProductId() {
        return productId;
    }

    public int getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public int getDayWeek() {
        return dayWeek;
    }

    public long getStartSaleDate() {
        return startSaleDate.toEpochSecond();
    }

    public long getEndSaleDate() {
        return endSaleDate.toEpochSecond();
    }

    public int getMaxStock() {
        return maxStock;
    }

    public List<ProductItemComponent> getComponents() {
        return components;
    }

    public EventFlag getEventFlag() {
        return eventsFlag;
    }

    public void enableEventFlag() {
        if (isFlag(EventFlag.EVENT)) {
            return;
        }
        eventsFlag = EventFlag.EVENT;
    }

    public void disableEventFlag() {
        if (!isFlag(EventFlag.EVENT)) {
            return;
        }
        eventsFlag = EventFlag.NONE;
    }

    public void enableBestFlag() {
        if (isFlag(EventFlag.BEST)) {
            return;
        }
        eventsFlag = EventFlag.BEST;
    }

    public void disableBestFlag() {
        if (!isFlag(EventFlag.BEST)) {
            return;
        }
        eventsFlag = EventFlag.NONE;
    }

    public boolean isFlag(final EventFlag g) {
        return eventsFlag == g;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void incBuyCount() {
        buyCount++;
    }

    public int getCurrentStock() {
        return DEFAULT_CURRENT_STOCK;
    }

    public int getStartSaleHour() {
        return startSaleDate.getHour();
    }

    public int getEndSaleHour() {
        return endSaleDate.getHour();
    }

    public int getStartSaleMin() {
        return startSaleDate.getMinute();
    }

    public int getEndSaleMin() {
        return endSaleDate.getMinute();
    }

    @Override
    public String toString() {
        return productId + ":" + productId;
    }

    public enum EventFlag {
        NONE,
        EVENT,
        BEST,
        EVENT_AND_BEST
    }
}