package org.mmocore.gameserver.model;

import org.mmocore.gameserver.object.components.items.ItemHolder;
import org.mmocore.gameserver.utils.Location;

public class TeleportLocation extends Location {
    private final ItemHolder item;
    private final int name;
    private final int castleId;

    public TeleportLocation(final int itemId, final long price, final int name, final int castleId) {
        this.item = new ItemHolder(itemId, price);
        this.name = name;
        this.castleId = castleId;
    }

    public long getPrice() {
        return item.getCount();
    }

    public ItemHolder getItem() {
        return item;
    }

    public int getName() {
        return name;
    }

    public int getCastleId() {
        return castleId;
    }
}
