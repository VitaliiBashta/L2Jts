package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Inventory;

import java.util.Map;

public class ShopPreviewInfo extends GameServerPacket {
    private final Map<Integer, Integer> itemlist;

    public ShopPreviewInfo(final Map<Integer, Integer> itemlist) {
        this.itemlist = itemlist;
    }

    @Override
    protected void writeData() {
        writeD(Inventory.PAPERDOLL_MAX);

        // Slots
        for (final int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER) {
            writeD(getFromList(PAPERDOLL_ID));
        }
    }

    private int getFromList(final int key) {
        return ((itemlist.get(key) != null) ? itemlist.get(key) : 0);
    }
}