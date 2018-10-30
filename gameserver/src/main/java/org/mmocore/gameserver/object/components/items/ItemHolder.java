package org.mmocore.gameserver.object.components.items;

/**
 * @author Java-man
 */
public class ItemHolder {
    public static final ItemHolder[] EMPTY_ITEM_HOLDER_ARRAY = new ItemHolder[0];

    private int itemId;
    private long count;

    public ItemHolder(final int itemId, final long count) {
        this.itemId = itemId;
        this.count = count;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(final int itemId) {
        this.itemId = itemId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ItemHolder that = (ItemHolder) o;

        if (count != that.count) {
            return false;
        }
        if (itemId != that.itemId) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemId;
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }
}
