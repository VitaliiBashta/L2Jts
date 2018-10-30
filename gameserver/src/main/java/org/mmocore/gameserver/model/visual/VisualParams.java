package org.mmocore.gameserver.model.visual;

import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Created by Hack
 * Date: 08.06.2017 0:39
 */
public class VisualParams {
    private ItemInstance item;
    private ItemInstance consumable;

    public ItemInstance getItem() {
        return item;
    }

    public void setItem(ItemInstance item) {
        this.item = item;
    }

    public ItemInstance getConsumable() {
        return consumable;
    }

    public void setConsumable(ItemInstance consumable) {
        this.consumable = consumable;
    }
}
