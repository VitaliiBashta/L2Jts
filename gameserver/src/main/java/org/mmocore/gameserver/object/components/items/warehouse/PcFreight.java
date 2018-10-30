package org.mmocore.gameserver.object.components.items.warehouse;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author VISTALL
 * @date 20:20/16.05.2011
 */
public class PcFreight extends Warehouse {
    public PcFreight(Player player) {
        super(player.getObjectId());
    }

    public PcFreight(int objectId) {
        super(objectId);
    }

    @Override
    public ItemInstance.ItemLocation getItemLocation() {
        return ItemInstance.ItemLocation.FREIGHT;
    }
}
