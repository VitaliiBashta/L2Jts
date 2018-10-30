package org.mmocore.gameserver.model.base;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.object.components.items.ItemAttributes;

public class MultiSellIngredient implements Cloneable {
    private int _itemId;
    private long _itemCount;
    private int _itemEnchant;
    private ItemAttributes _itemAttributes;
    private boolean _mantainIngredient;
    private int _visual;
    private int _flag;
    private int _lifeTime;
    private boolean isCostume;

    public MultiSellIngredient(final int itemId, final long itemCount) {
        this(itemId, itemCount, 0);
    }

    public MultiSellIngredient(final int itemId, final long itemCount, final int enchant) {
        _itemId = itemId;
        _itemCount = itemCount;
        _itemEnchant = enchant;
        _mantainIngredient = false;
        _itemAttributes = new ItemAttributes();
    }

    @Override
    public MultiSellIngredient clone() {
        final MultiSellIngredient mi = new MultiSellIngredient(_itemId, _itemCount, _itemEnchant);
        mi.setMantainIngredient(_mantainIngredient);
        mi.setItemAttributes(_itemAttributes.clone());
        return mi;
    }

    /**
     * @return Returns the itemId.
     */
    public int getItemId() {
        return _itemId;
    }

    /**
     * @param itemId The itemId to set.
     */
    public void setItemId(final int itemId) {
        _itemId = itemId;
    }

    /**
     * @return Returns the itemCount.
     */
    public long getItemCount() {
        return _itemCount;
    }

    /**
     * @param itemCount The itemCount to set.
     */
    public void setItemCount(final long itemCount) {
        _itemCount = itemCount;
    }

    /**
     * Returns if item is stackable
     *
     * @return boolean
     */
    public boolean isStackable() {
        return _itemId <= 0 || ItemTemplateHolder.getInstance().getTemplate(_itemId).isStackable();
    }

    /**
     * @return Returns the itemEnchant.
     */
    public int getItemEnchant() {
        return _itemEnchant;
    }

    /**
     * @param itemEnchant The itemEnchant to set.
     */
    public void setItemEnchant(final int itemEnchant) {
        _itemEnchant = itemEnchant;
    }

    public ItemAttributes getItemAttributes() {
        return _itemAttributes;
    }

    public void setItemAttributes(final ItemAttributes attr) {
        _itemAttributes = attr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (_itemCount ^ _itemCount >>> 32);
        for (final Element e : Element.VALUES) {
            result = prime * result + _itemAttributes.getValue(e);
        }
        result = prime * result + _itemEnchant;
        result = prime * result + _itemId;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultiSellIngredient other = (MultiSellIngredient) obj;
        if (_itemId != other._itemId) {
            return false;
        }
        if (_itemCount != other._itemCount) {
            return false;
        }
        if (_itemEnchant != other._itemEnchant) {
            return false;
        }
        for (final Element e : Element.VALUES) {
            if (_itemAttributes.getValue(e) != other._itemAttributes.getValue(e)) {
                return false;
            }
        }
        return true;
    }

    public boolean getMantainIngredient() {
        return _mantainIngredient;
    }

    public void setMantainIngredient(final boolean mantainIngredient) {
        _mantainIngredient = mantainIngredient;
    }

    public int getVisual() {
        return _visual;
    }

    public void setVisual(int visual) {
        _visual = visual;
        ;
    }

    public int getFlag() {
        return _flag;
    }

    public void setFlag(int flag) {
        _flag = flag;
        ;
    }

    public int getItemLifeTime() {
        return _lifeTime;
    }

    public void setItemLifeTime(int lifeTime) {
        _lifeTime = lifeTime;
    }

    public boolean isCostume() {
        return isCostume;
    }

    public void setCostume(boolean costume) {
        isCostume = costume;
    }

}