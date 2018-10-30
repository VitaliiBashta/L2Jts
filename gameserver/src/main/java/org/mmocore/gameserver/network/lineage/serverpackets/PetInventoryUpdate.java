package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public class PetInventoryUpdate extends GameServerPacket {
    public static final int UNCHANGED = 0;
    public static final int ADDED = 1;
    public static final int MODIFIED = 2;
    public static final int REMOVED = 3;

    private final List<ItemInfo> items = new ArrayList<>(1);

    public PetInventoryUpdate() {
    }

    public PetInventoryUpdate addNewItem(final ItemInstance item) {
        addItem(item).setLastChange(ADDED);
        return this;
    }

    public PetInventoryUpdate addModifiedItem(final ItemInstance item) {
        addItem(item).setLastChange(MODIFIED);
        return this;
    }

    public PetInventoryUpdate addRemovedItem(final ItemInstance item) {
        addItem(item).setLastChange(REMOVED);
        return this;
    }

    private ItemInfo addItem(final ItemInstance item) {
        final ItemInfo info;
        items.add(info = new ItemInfo(item));
        return info;
    }

    @Override
    protected final void writeData() {
        writeH(items.size());
        for (final ItemInfo temp : items) {
            writeH(temp.getLastChange());
            writeItemInfo(temp);
        }
    }
}