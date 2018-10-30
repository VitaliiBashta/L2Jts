package org.mmocore.gameserver.object.components.player.recipe;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.database.dao.impl.CharacterRecipebookDAO;
import org.mmocore.gameserver.object.Player;

import java.util.Collection;
import java.util.Collections;

/**
 * Create by Mangol on 21.12.2015.
 */
public class RecipeComponent {
    private static final CharacterRecipebookDAO dao = CharacterRecipebookDAO.getInstance();
    private final TIntObjectHashMap<Recipe> recipebook = new TIntObjectHashMap<>();
    private final TIntObjectHashMap<Recipe> commonrecipebook = new TIntObjectHashMap<>();
    private final Player player;

    public RecipeComponent(Player player) {
        this.player = player;
    }

    public void restore() {
        dao.restoreRecipeBook(player.getObjectId(), this);
    }

    public void unregisterRecipe(final int recipeId) {
        dao.unregisterRecipe(player.getObjectId(), recipeId);
    }

    public void registerRecipe(final int recipeId) {
        dao.registerRecipe(player.getObjectId(), recipeId);
    }

    public Collection<Recipe> getDwarvenRecipeBook() {
        return Collections.unmodifiableCollection(recipebook.valueCollection());
    }

    public Collection<Recipe> getCommonRecipeBook() {
        return Collections.unmodifiableCollection(commonrecipebook.valueCollection());
    }

    public int recipesCount() {
        return commonrecipebook.size() + recipebook.size();
    }

    public boolean hasRecipe(final Recipe id) {
        return recipebook.containsValue(id) || commonrecipebook.containsValue(id);
    }

    public boolean findRecipe(final int id) {
        return recipebook.containsKey(id) || commonrecipebook.containsKey(id);
    }

    public Recipe getRecipe(final int id) {
        Recipe recipe = recipebook.get(id);
        if (recipe == null)
            return commonrecipebook.get(id);
        return recipe;
    }

    public void addCommonRecipe(final Recipe recipe) {
        commonrecipebook.put(recipe.getId(), recipe);
    }

    public void addDwarfRecipe(final Recipe recipe) {
        recipebook.put(recipe.getId(), recipe);
    }

    public void removeCommonRecipe(final int id) {
        commonrecipebook.remove(id);
    }

    public void removeDwarvenRecipe(final int id) {
        recipebook.remove(id);
    }
}
