package org.mmocore.gameserver.model.base;

import java.util.ArrayList;
import java.util.List;

public class MultiSellEntry {
    private final List<MultiSellIngredient> _ingredients = new ArrayList<>();
    private final List<MultiSellIngredient> _production = new ArrayList<>();
    private int _entryId;
    private long _tax;

    public MultiSellEntry() {
    }

    public MultiSellEntry(final int id) {
        _entryId = id;
    }

    public MultiSellEntry(final int id, final int product, final int prod_count, final int enchant) {
        _entryId = id;
        addProduct(new MultiSellIngredient(product, prod_count, enchant));
    }

    /**
     * @return Returns the entryId.
     */
    public int getEntryId() {
        return _entryId;
    }

    /**
     * @param entryId The entryId to set.
     */
    public void setEntryId(final int entryId) {
        _entryId = entryId;
    }

    /**
     * @param ingredients The ingredients to set.
     */
    public void addIngredient(final MultiSellIngredient ingredient) {
        if (ingredient.getItemCount() > 0) {
            _ingredients.add(ingredient);
        }
    }

    /**
     * @return Returns the ingredients.
     */
    public List<MultiSellIngredient> getIngredients() {
        return _ingredients;
    }

    /**
     * @param ingredients The ingredients to set.
     */
    public void addProduct(final MultiSellIngredient ingredient) {
        _production.add(ingredient);
    }

    /**
     * @return Returns the ingredients.
     */
    public List<MultiSellIngredient> getProduction() {
        return _production;
    }

    public long getTax() {
        return _tax;
    }

    public void setTax(final long tax) {
        _tax = tax;
    }

    @Override
    public int hashCode() {
        return _entryId;
    }

    @Override
    public MultiSellEntry clone() {
        final MultiSellEntry ret = new MultiSellEntry(_entryId);
        for (final MultiSellIngredient i : _ingredients) {
            ret.addIngredient(i.clone());
        }
        for (final MultiSellIngredient i : _production) {
            ret.addProduct(i.clone());
        }
        return ret;
    }
}