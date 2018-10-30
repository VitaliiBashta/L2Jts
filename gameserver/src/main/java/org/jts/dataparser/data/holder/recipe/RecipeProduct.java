package org.jts.dataparser.data.holder.recipe;

import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.pch.LinkerFactory;

/**
 * Create by Mangol on 21.12.2015.
 */
public class RecipeProduct {
    private final LinkerFactory linkerFactory = LinkerFactory.getInstance();

    @StringValue(withoutName = true)
    private String nameProduct;
    @IntValue(withoutName = true)
    private int count;
    @IntValue(withoutName = true)
    private int chance;

    public int getChance() {
        return chance;
    }

    public int getIdProduct() {
        return linkerFactory.findClearValue(nameProduct);
    }

    public int getCount() {
        return count;
    }
}
