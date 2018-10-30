package org.mmocore.gameserver.templates.custom.community.leaseTransform;

/**
 * @author Mangol
 * @since 03.02.2016
 */
public class TimesTemplate {
    private final int key;
    private final int minute;
    private final int itemId;
    private final int itemCount;

    public TimesTemplate(int key, int minute, int itemId, int itemCount) {
        this.key = key;
        this.minute = minute;
        this.itemId = itemId;
        this.itemCount = itemCount;
    }

    public int getKey() {
        return key;
    }

    public int getMinute() {
        return minute;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemCount() {
        return itemCount;
    }
}
