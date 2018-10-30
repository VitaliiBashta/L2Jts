package org.mmocore.gameserver.object.components.items;

import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class ItemContainer {
    protected final List<ItemInstance> _items = new ArrayList<>();
    /**
     * Блокировка для чтения/записи вещей из списка и внешних операций
     */
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Lock readLock = lock.readLock();
    protected final Lock writeLock = lock.writeLock();

    protected ItemContainer() {
    }

    public int getSize() {
        return _items.size();
    }

    public ItemInstance[] getItems() {
        readLock();
        try {
            return _items.toArray(new ItemInstance[_items.size()]);
        } finally {
            readUnlock();
        }
    }

    public void clear() {
        writeLock();
        try {
            _items.clear();
        } finally {
            writeUnlock();
        }
    }

    public final void writeLock() {
        writeLock.lock();
    }

    public final void writeUnlock() {
        writeLock.unlock();
    }

    public final void readLock() {
        readLock.lock();
    }

    public final void readUnlock() {
        readLock.unlock();
    }

    /**
     * Найти вещь по objectId
     *
     * @param objectId
     * @return вещь, если найдена, либо null если не найдена
     */
    public ItemInstance getItemByObjectId(int objectId) {
        readLock();
        try {
            for (final ItemInstance item : _items) {
                if (item.getObjectId() == objectId)
                    return item;
            }
        } finally {
            readUnlock();
        }

        return null;
    }

    /**
     * Найти первую вещь по itemId
     *
     * @param itemId
     * @return вещь, если найдена, либо null если не найдена
     */
    public ItemInstance getItemByItemId(int itemId) {
        readLock();
        try {
            for (ItemInstance item : _items) {
                if (item.getItemId() == itemId)
                    return item;
            }
        } finally {
            readUnlock();
        }

        return null;
    }

    /**
     * Найти все вещи по itemId
     *
     * @param itemId
     * @return Список найденых вещей
     */
    public List<ItemInstance> getItemsByItemId(int itemId) {
        List<ItemInstance> result = new ArrayList<>();

        readLock();
        try {
            result.addAll(_items.stream().filter(item -> item.getItemId() == itemId).collect(Collectors.toList()));
        } finally {
            readUnlock();
        }

        return result;
    }

    public long getCountOf(int itemId) {
        long count = 0L;
        readLock();
        try {
            for (ItemInstance item : _items) {
                if (item.getItemId() == itemId)
                    count = Math.addExact(count, item.getCount());
            }
        } finally {
            readUnlock();
        }
        return count;
    }

    public boolean containsItems(final int... itemIds) {
        for (final int itemId : itemIds) {
            if (!containsItem(itemId)) {
                return false;
            }
        }

        return true;
    }

    public boolean containsItem(final int itemId) {
        readLock();

        try {
            for (final ItemInstance item : _items) {
                if (item.getItemId() == itemId) {
                    return true;
                }
            }
        } finally {
            readUnlock();
        }

        return false;
    }

    /**
     * Создать вещь и добавить в список, либо увеличить количество вещи в инвентаре
     *
     * @param itemId - идентификатор itemId вещи
     * @param count  - количество для создания, либо увеличения
     * @return созданная вещь
     */
    public ItemInstance addItem(int itemId, long count) {
        if (count < 1) {
            return null;
        }

        ItemInstance item;

        writeLock();
        try {
            item = getItemByItemId(itemId);

            if (item != null && item.isStackable()) {
                synchronized (item) {
                    try {
                        item.setCount(Math.addExact(item.getCount(), count));
                    } catch (ArithmeticException e) {
                        item.setCount(Long.MAX_VALUE);
                    }
                    onModifyItem(item);
                }
            } else {
                item = ItemFunctions.createItem(itemId);
                item.setCount(count);

                _items.add(item);
                onAddItem(item);
            }
        } finally {
            writeUnlock();
        }

        return item;
    }

    /**
     * Добавить вещь в список.<br>
     * При добавлении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
     * <br>
     * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
     *
     * @return вещь, полученая в результате добавления, null если не найдена
     */
    public ItemInstance addItem(ItemInstance item) {
        if (item == null) {
            return null;
        }

        if (item.getCount() < 1) {
            return null;
        }

        ItemInstance result = null;

        writeLock();
        try {
            if (getItemByObjectId(item.getObjectId()) != null) {
                return null;
            }

            if (item.isStackable()) {
                int itemId = item.getItemId();
                result = getItemByItemId(itemId);
                if (result != null) {
                    synchronized (result) {
                        // увеличить количество в стопке
                        try {
                            result.setCount(Math.addExact(item.getCount(), result.getCount()));
                        } catch (ArithmeticException e) {
                            result.setCount(Long.MAX_VALUE);
                        }
                        onModifyItem(result);
                        onDestroyItem(item);
                    }
                }
            }

            if (result == null) {
                _items.add(item);
                result = item;

                onAddItem(result);
            }
        } finally {
            writeUnlock();
        }

        return result;
    }

    /**
     * Удаляет вещь из списка, либо уменьшает количество вещи по objectId
     *
     * @param objectId - идентификатор objectId вещи
     * @param count    - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
     * @return вещь, полученая в результате удаления, null если не найдена
     */
    public ItemInstance removeItemByObjectId(int objectId, long count) {
        if (count < 1) {
            return null;
        }

        ItemInstance result;

        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByObjectId(objectId)) == null) {
                return null;
            }

            synchronized (item) {
                result = removeItem(item, count);
            }
        } finally {
            writeUnlock();
        }

        return result;
    }

    /**
     * Удаляет вещь из списка, либо уменьшает количество первой найденной вещи по itemId
     *
     * @param itemId - идентификатор itemId
     * @param count  - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
     * @return вещь, полученая в результате удаления, null если не найдена
     */
    public ItemInstance removeItemByItemId(int itemId, long count) {
        if (count < 1) {
            return null;
        }

        ItemInstance result;

        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByItemId(itemId)) == null) {
                return null;
            }

            synchronized (item) {
                result = removeItem(item, count);
            }
        } finally {
            writeUnlock();
        }

        return result;
    }

    /**
     * Удаляет вещь из списка, либо уменьшает количество вещи.<br>
     * При удалении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
     * <br>
     * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
     *
     * @param item  - вещь для удаления
     * @param count - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
     * @return вещь, полученая в результате удаления
     */
    public ItemInstance removeItem(ItemInstance item, long count) {
        if (item == null) {
            return null;
        }

        if (count < 1) {
            return null;
        }

        if (item.getCount() < count) {
            return null;
        }

        writeLock();
        try {
            if (!_items.contains(item)) {
                return null;
            }

            if (item.getCount() > count) {
                item.setCount(item.getCount() - count);
                onModifyItem(item);

                ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), item.getItemId());
                newItem.setCount(count);

                return newItem;
            } else {
                return removeItem(item);
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * Удаляет вещь из списка.<br>
     * При удалении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
     * <br>
     * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
     *
     * @param item - вещь для удаления
     * @return вещь, полученая в результате удаления
     */
    public ItemInstance removeItem(ItemInstance item) {
        if (item == null) {
            return null;
        }

        writeLock();
        try {
            if (!_items.remove(item)) {
                return null;
            }

            onRemoveItem(item);

            return item;
        } finally {
            writeUnlock();
        }
    }

    /**
     * Уничтожить вещь из списка, либо снизить количество по идентификатору objectId
     *
     * @param objectId
     * @param count    - количество для удаления
     * @return true, если количество было снижено или вещь была уничтожена
     */
    public boolean destroyItemByObjectId(int objectId, long count) {
        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByObjectId(objectId)) == null) {
                return false;
            }

            synchronized (item) {
                return destroyItem(item, count);
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * Уничтожить вещь из списка, либо снизить количество по идентификатору itemId
     *
     * @param itemId
     * @param count  - количество для удаления
     * @return true, если количество было снижено или вещь была уничтожена
     */
    public boolean destroyItemByItemId(int itemId, long count) {
        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByItemId(itemId)) == null) {
                return false;
            }

            synchronized (item) {
                return destroyItem(item, count);
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * Уничтожить вещь из списка, либо снизить количество<br>
     * <br>
     * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
     *
     * @param item
     * @param count - количество для удаления
     * @return true, если количество было снижено или вещь была уничтожена
     */
    public boolean destroyItem(ItemInstance item, long count) {
        if (item == null) {
            return false;
        }

        if (count < 1) {
            return false;
        }

        if (item.getCount() < count) {
            return false;
        }

        writeLock();
        try {
            if (!_items.contains(item)) {
                return false;
            }

            if (item.getCount() > count) {
                item.setCount(item.getCount() - count);
                onModifyItem(item);

                return true;
            } else {
                return destroyItem(item);
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * Удаляет вещь из списка.<br>
     * <br>
     * <b><font color="red">Должно выполнятся в блоке synchronized(item)</font></b>
     *
     * @param item - вещь для удаления
     * @return вещь, полученая в результате удаления
     */
    public boolean destroyItem(ItemInstance item) {
        if (item == null) {
            return false;
        }

        writeLock();
        try {
            if (!_items.remove(item)) {
                return false;
            }

            onRemoveItem(item);
            onDestroyItem(item);

            return true;
        } finally {
            writeUnlock();
        }
    }

    protected abstract void onAddItem(ItemInstance item);

    protected abstract void onModifyItem(ItemInstance item);

    protected abstract void onRemoveItem(ItemInstance item);

    protected abstract void onDestroyItem(ItemInstance item);
}
