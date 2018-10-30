package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class GMViewItemList extends GameServerPacket {
    private final int size;
    private final ItemInstance[] items;
    private final int limit;
    private final String name;

    public GMViewItemList(final Player cha, final ItemInstance[] items, final int size) {
        this.size = size;
        this.items = items;
        name = cha.getName();
        limit = cha.getInventoryLimit();
    }

    @Override
    protected final void writeData() {
        writeS(name);
        writeD(limit); //c4?
        writeH(1); // show window ??

        writeH(size);
        for (final ItemInstance temp : items) {
            if (!temp.getTemplate().isQuest()) {
                writeItemInfo(temp);
            }
        }
    }
}