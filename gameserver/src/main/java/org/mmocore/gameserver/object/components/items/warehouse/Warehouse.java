package org.mmocore.gameserver.object.components.items.warehouse;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.object.components.items.ItemContainer;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate.ItemClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Warehouse extends ItemContainer {
    protected final int _ownerId;

    protected Warehouse(int ownerId) {
        _ownerId = ownerId;
    }

    public int getOwnerId() {
        return _ownerId;
    }

    public abstract ItemLocation getItemLocation();

    public ItemInstance[] getItems(ItemClass itemClass) {
        List<ItemInstance> result = new ArrayList<>();

        readLock();
        try {
            result.addAll(_items.stream().filter(item -> itemClass == null || itemClass == ItemClass.ALL || item.getItemClass() == itemClass).collect(Collectors.toList()));
        } finally {
            readUnlock();
        }

        return result.toArray(new ItemInstance[result.size()]);
    }

    public long getCountOfAdena() {
        return getCountOf(ItemTemplate.ITEM_ID_ADENA);
    }

    @Override
    protected void onAddItem(ItemInstance item) {
        item.setOwnerId(getOwnerId());
        item.setLocation(getItemLocation());
        item.setLocData(0);
        if (item.getJdbcState().isSavable()) {
            item.save();
        } else {
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();
        }
    }

    @Override
    protected void onModifyItem(ItemInstance item) {
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
    }

    @Override
    protected void onRemoveItem(ItemInstance item) {
        item.setLocData(-1);
    }

    @Override
    protected void onDestroyItem(ItemInstance item) {
        item.setCount(0L);
        item.delete();
    }

    public void restore() {
        final int ownerId = getOwnerId();

        writeLock();
        try {
            final Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(ownerId, getItemLocation());
            _items.addAll(items);
        } finally {
            writeUnlock();
        }
    }

    public enum WarehouseType {
        NONE,
        PRIVATE,
        CLAN,
        CASTLE,
        FREIGHT
    }

    public static class ItemClassComparator implements Comparator<ItemInstance> {
        private static final Comparator<ItemInstance> instance = new ItemClassComparator();

        public static Comparator<ItemInstance> getInstance() {
            return instance;
        }

        @Override
        public int compare(ItemInstance o1, ItemInstance o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            int diff = o1.getItemClass().ordinal() - o2.getItemClass().ordinal();
            if (diff == 0) {
                diff = o1.getCrystalType().ordinal() - o2.getCrystalType().ordinal();
            }
            if (diff == 0) {
                diff = o1.getItemId() - o2.getItemId();
            }
            if (diff == 0) {
                diff = o1.getEnchantLevel() - o2.getEnchantLevel();
            }
            return diff;
        }
    }
}