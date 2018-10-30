package org.mmocore.gameserver.object.components.items.warehouse;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;

public class PcWarehouse extends Warehouse {
    public PcWarehouse(Player owner) {
        super(owner.getObjectId());
    }

    public PcWarehouse(int ownerId) {
        super(ownerId);
    }

    @Override
    public ItemLocation getItemLocation() {
        return ItemLocation.WAREHOUSE;
    }
}