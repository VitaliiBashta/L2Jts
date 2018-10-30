package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ManufactureItem;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;

import java.util.Collection;
import java.util.List;


public class RecipeShopManageList extends GameServerPacket {
    private final List<ManufactureItem> createList;
    private final Collection<Recipe> recipes;
    private final int sellerId;
    private final long adena;
    private final boolean isDwarven;

    public RecipeShopManageList(final Player seller, final boolean isDwarvenCraft) {
        sellerId = seller.getObjectId();
        adena = seller.getAdena();
        isDwarven = isDwarvenCraft;
        final RecipeComponent recipeComponent = seller.getRecipeComponent();
        if (isDwarven) {
            recipes = recipeComponent.getDwarvenRecipeBook();
        } else {
            recipes = recipeComponent.getCommonRecipeBook();
        }
        createList = seller.getCreateList();
        createList.stream().filter(mi -> !recipeComponent.findRecipe(mi.getRecipeId())).forEach(createList::remove);
    }

    @Override
    protected final void writeData() {
        writeD(sellerId);
        writeD((int) Math.min(adena, Integer.MAX_VALUE)); //FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
        writeD(isDwarven ? 0x00 : 0x01);
        writeD(recipes.size());
        int i = 1;
        for (final Recipe recipe : recipes) {
            writeD(recipe.getId());
            writeD(i++);
        }
        writeD(createList.size());
        for (final ManufactureItem mi : createList) {
            writeD(mi.getRecipeId());
            writeD(0x00); //??
            writeQ(mi.getCost());
        }
    }
}