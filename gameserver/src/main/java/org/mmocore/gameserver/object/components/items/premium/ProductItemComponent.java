package org.mmocore.gameserver.object.components.items.premium;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * @author VISTALL
 * @modified KilRoy
 */
public class ProductItemComponent {
    private final ItemTemplate _template;
    private final int _count;

    public ProductItemComponent(final int itemId, final int count) {
        _template = ItemTemplateHolder.getInstance().getTemplate(itemId);
        _count = count;
    }

    public int getItemId() {
        return _template.getItemId();
    }

    public int getCount() {
        return _count;
    }

    public int getWeight() {
        return _template.getWeight();
    }

    public boolean isDropable() {
        return _template.isDropable();
    }
}