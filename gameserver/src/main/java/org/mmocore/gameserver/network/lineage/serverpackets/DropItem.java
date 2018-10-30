package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Location;

public class DropItem extends GameServerPacket {
    private final Location loc;
    private final int playerId;
    private final int item_obj_id;
    private final int item_id;
    private final int stackable;
    private final long count;

    /**
     * Constructor<?> of the DropItem server packet
     *
     * @param item     : L2ItemInstance designating the item
     * @param playerId : int designating the player ID who dropped the item
     */
    public DropItem(final ItemInstance item, final int playerId) {
        this.playerId = playerId;
        item_obj_id = item.getObjectId();
        item_id = item.getItemId();
        loc = item.getLoc();
        stackable = item.isStackable() ? 1 : 0;
        count = item.getCount();
    }

    @Override
    protected final void writeData() {
        writeD(playerId);
        writeD(item_obj_id);
        writeD(item_id);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z + GeodataConfig.CLIENT_Z_SHIFT);
        writeD(stackable);
        writeQ(count);
        writeD(1); // unknown
    }
}