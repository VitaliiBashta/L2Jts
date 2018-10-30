package org.mmocore.gameserver.templates.custom.premium.component;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public class PremiumTime {
    private int id;
    private int days;
    private int hour;
    private int minute;
    private int itemId;
    private long price;

    public PremiumTime(final int id, final int days, final int hour, final int minute, final int itemId, final long price) {
        this.id = id;
        this.days = days;
        this.hour = hour;
        this.minute = minute;
        this.itemId = itemId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getDays() {
        return days;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getItemId() {
        return itemId;
    }

    public long getPrice() {
        return price;
    }
}
