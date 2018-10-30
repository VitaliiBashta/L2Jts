package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * 15
 * ee cc 11 43 		object id
 * 39 00 00 00 		item id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 01 00 00 00 		show item count
 * 7a 00 00 00      count                                         .
 * <p/>
 * format  dddddddd
 */
public class SpawnItem extends GameServerPacket {
    private final int objectId;
    private final int itemId;
    private final int x;
    private final int y;
    private final int z;
    private final int stackable;
    private final long count;

    public SpawnItem(final ItemInstance item) {
        objectId = item.getObjectId();
        itemId = item.getItemId();
        x = item.getX();
        y = item.getY();
        z = item.getZ();
        stackable = item.isStackable() ? 0x01 : 0x00;
        count = item.getCount();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(itemId);

        writeD(x);
        writeD(y);
        writeD(z + GeodataConfig.CLIENT_Z_SHIFT);
        writeD(stackable);
        writeQ(count);
        writeD(0x00); //c2
    }
}