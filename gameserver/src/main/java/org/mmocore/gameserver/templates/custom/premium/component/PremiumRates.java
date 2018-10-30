package org.mmocore.gameserver.templates.custom.premium.component;

import org.mmocore.gameserver.templates.custom.premium.PremiumType;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public class PremiumRates {
    private final PremiumType type;
    private int id;
    private double value = 1.;
    private int itemId;
    private long price;

    public PremiumRates(final PremiumType type, final int id, final double value, final int itemId, final long price) {
        this.type = type;
        this.id = id;
        this.value = value;
        this.itemId = itemId;
        this.price = price;
    }

    public PremiumRates(final PremiumType type, final double value) {
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public int getItemId() {
        return itemId;
    }

    public long getPrice() {
        return price;
    }

    public PremiumType getType() {
        return type;
    }
}
