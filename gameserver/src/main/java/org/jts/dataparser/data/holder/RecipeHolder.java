package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.commons.data.AbstractHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Create by Mangol on 21.12.2015.
 */
public class RecipeHolder extends AbstractHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeHolder.class);
    private static final RecipeHolder INSTANCE = new RecipeHolder();
    @Element(start = "recipe_begin", end = "recipe_end")
    public static List<Recipe> recipeList;
    private Map<Integer, Recipe> recipeIdMap;
    private Map<Integer, Recipe> recipeItemIdMap;

    public static RecipeHolder getInstance() {
        return INSTANCE;
    }

    @Override
    public void afterParsing() {
        super.afterParsing();
        recipeIdMap = recipeList.stream().collect(Collectors.toMap(Recipe::getId, recipe -> recipe));
        recipeItemIdMap = recipeList.stream().collect(Collectors.toMap(Recipe::getItemId, recipe -> recipe));
    }

    public Map<Integer, Recipe> getRecipeId() {
        return recipeIdMap;
    }

    public Map<Integer, Recipe> getRecipeItemId() {
        return recipeItemIdMap;
    }

    public Recipe getRecipeId(final int id) {
        final Optional<Recipe> recipe = Optional.ofNullable(recipeIdMap.get(id));
        if (recipe.isPresent()) {
            return recipe.get();
        } else {
            LOGGER.error("recipe id - " + id + " map null");
        }
        return null;
    }

    public Recipe getRecipeItemId(final int itemId) {
        final Optional<Recipe> recipe = Optional.ofNullable(recipeItemIdMap.get(itemId));
        if (recipe.isPresent()) {
            return recipe.get();
        } else {
            LOGGER.error("recipe item id - " + itemId + " map null");
        }
        return null;
    }

    @Override
    public int size() {
        return recipeIdMap.size();
    }

    @Override
    public void clear() {
        //
    }
}
