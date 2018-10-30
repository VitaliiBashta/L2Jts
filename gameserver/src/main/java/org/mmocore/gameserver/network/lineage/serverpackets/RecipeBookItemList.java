package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;

import java.util.Collection;

public class RecipeBookItemList extends GameServerPacket {
    private final Collection<Recipe> recipes;
    private final int isDwarvenCraft;
    private final int currentMp;

    public RecipeBookItemList(final Player player, final boolean isDwarvenCraft) {
        this.isDwarvenCraft = isDwarvenCraft ? 0 : 1;
        currentMp = (int) player.getCurrentMp();
        final RecipeComponent recipeComponent = player.getRecipeComponent();
        if (isDwarvenCraft)
            recipes = recipeComponent.getDwarvenRecipeBook();
        else
            recipes = recipeComponent.getCommonRecipeBook();
    }

    @Override
    protected final void writeData() {
        writeD(isDwarvenCraft);
        writeD(currentMp);

        writeD(recipes.size());

        for (final Recipe recipe : recipes) {
            writeD(recipe.getId());
            writeD(1); //??
        }
    }
}