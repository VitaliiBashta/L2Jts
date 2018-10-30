package org.jts.dataparser.data.holder.recipe;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.mmocore.commons.utils.Rnd;

import java.util.Optional;

/**
 * Create by Mangol on 21.12.2015.
 */
public class Recipe {
    @StringValue(withoutName = true)
    private String nameRecipe;
    @IntValue(withoutName = true)
    private int id;
    @IntValue
    private int level;
    @ObjectArray
    private RecipeMaterial[] material;
    @StringArray
    private String[] catalyst = new String[0];
    @ObjectArray
    private RecipeProduct[] product;
    @ObjectArray
    private RecipeNpcFee[] npc_fee = new RecipeNpcFee[0];
    @IntValue
    private int mp_consume;
    @IntValue
    private int success_rate;
    @IntValue
    private int item_id;
    @IntValue
    private int iscommonrecipe;

    public String getNameRecipe() {
        return nameRecipe;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public RecipeMaterial[] getMaterials() {
        return material;
    }

    public String[] getCatalyst() {
        return catalyst;
    }

    public RecipeProduct[] getProducts() {
        return product;
    }

    public RecipeNpcFee[] getNpcFee() {
        return npc_fee;
    }

    public int getMpConsume() {
        return mp_consume;
    }

    public int getSuccessRate() {
        return success_rate;
    }

    public int getItemId() {
        return item_id;
    }

    public boolean isCommonRecipe() {
        return iscommonrecipe == 1;
    }

    public int getIsCommonRecipe() {
        return iscommonrecipe;
    }

    public Optional<RecipeProduct> getRandomProduct(double mul) {
        if (Rnd.chance(getSuccessRate() * mul)) {
            int chancesAmount = 0;
            for (final RecipeProduct prod : getProducts()) {
                chancesAmount += prod.getChance();
                if (Rnd.chance(chancesAmount)) {
                    return Optional.of(prod);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<RecipeProduct> getRandomProduct() {
        return getRandomProduct(1);
    }
}
