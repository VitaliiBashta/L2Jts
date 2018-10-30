package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class PetItemList extends GameServerPacket {
    private final ItemInstance[] items;

    public PetItemList(final PetInstance cha) {
        items = cha.getInventory().getItems();
    }

    @Override
    protected final void writeData() {
        writeH(items.length);
        for (final ItemInstance item : items) {
            writeItemInfo(item);
        }
    }
}