package org.mmocore.gameserver.model.entity.itemauction;

import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author n0nam3
 */
public final class AuctionItem extends ItemInfo {
    private final int itemId;
    private final int ammount;
    private final long price;

    public AuctionItem(final int itemId, final int ammount, final long price, final int enchanted) {
        this.itemId = itemId;
        this.ammount = ammount;
        this.price = price;

        setObjectId(itemId);
        setItemId(itemId);
        setCount(ammount);
        setEnchantLevel(enchanted);
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    public int getAmmount() {
        return ammount;
    }

    public long getPrice() {
        return price;
    }

    public final ItemInstance createNewItemInstance() {
        final ItemInstance item = ItemFunctions.createItem(getItemId());
        item.setEnchantLevel(getEnchantLevel());

        return item;
    }
}