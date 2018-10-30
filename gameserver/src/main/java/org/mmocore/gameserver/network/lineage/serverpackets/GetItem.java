package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Location;

/**
 * 0000: 17  1a 95 20 48  9b da 12 40  44 17 02 00  03 f0 fc ff  98 f1 ff ff                                     .....
 * format  ddddd
 */
public class GetItem extends GameServerPacket {
    private final int playerId;
    private final int itemObjId;
    private final Location loc;

    public GetItem(final ItemInstance item, final int playerId) {
        itemObjId = item.getObjectId();
        loc = item.getLoc();
        this.playerId = playerId;
    }

    @Override
    protected final void writeData() {
        writeD(playerId);
        writeD(itemObjId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
    }
}