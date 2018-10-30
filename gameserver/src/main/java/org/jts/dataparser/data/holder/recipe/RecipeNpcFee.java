package org.jts.dataparser.data.holder.recipe;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.common.ItemName_Count;

/**
 * Create by Mangol on 21.12.2015.
 */
public class RecipeNpcFee {
    @ObjectArray
    private ItemName_Count[] productInfos;
    @ObjectArray
    private ItemName_Count[] ingredientInfos;

    public ItemName_Count[] getProductInfos() {
        return productInfos;
    }

    public ItemName_Count[] getIngredientInfos() {
        return ingredientInfos;
    }
}
