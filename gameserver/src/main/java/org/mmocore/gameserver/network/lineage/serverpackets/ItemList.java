package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.LockType;

public class ItemList extends GameServerPacket {
    private final int _size;
    private final ItemInstance[] _items;
    private final boolean _showWindow;

    private final LockType _lockType;
    private final int[] _lockItems;

    public ItemList(final int size, final ItemInstance[] items, final boolean showWindow, final LockType lockType, final int[] lockItems) {
        _size = size;
        _items = items;
        _showWindow = showWindow;
        _lockType = lockType;
        _lockItems = lockItems;
    }

    @Override
    protected final void writeData() {
        writeH(_showWindow ? 1 : 0);

        writeH(_size);
        for (final ItemInstance temp : _items) {
            if (temp.getTemplate().isQuest()) {
                continue;
            }

            writeItemInfo(temp);
        }

        writeH(_lockItems.length);
        if (_lockItems.length > 0) {
            writeC(_lockType.ordinal());
            for (final int i : _lockItems) {
                writeD(i);
            }
        }
    }
}