package org.mmocore.gameserver.object.components.player.community;

import org.mmocore.gameserver.utils.Location;

/**
 * Create by Mangol on 12.12.2015.
 */
public class TeleportPoint {
    private final int id;
    private final String name;
    private final int priceId;
    private final int priceCount;
    private final int minLevel;
    private final int maxLevel;
    private final boolean pk;
    private final boolean premium;
    private final int premiumPriceId;
    private final int premiumPriceCount;
    private final Location location;
    private final boolean confirm;

    public TeleportPoint(final int id, final String name, final int priceId, final int count, final int minLevel, final int maxLevel, final boolean pk, final boolean premium, final int premiumPriceId, final int premiumCount, final Location location, boolean confirm) {
        this.id = id;
        this.name = name;
        this.priceId = priceId;
        this.priceCount = count;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.pk = pk;
        this.premium = premium;
        this.premiumPriceId = premiumPriceId;
        this.premiumPriceCount = premiumCount;
        this.location = location;
        this.confirm = confirm;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriceId() {
        return priceId;
    }

    public int getPriceCount() {
        return priceCount;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isPk() {
        return pk;
    }

    public boolean isPremium() {
        return premium;
    }

    public int getPremiumPriceId() {
        return premiumPriceId;
    }

    public int getPremiumPriceCount() {
        return premiumPriceCount;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isConfirm() {
        return confirm;
    }
}
