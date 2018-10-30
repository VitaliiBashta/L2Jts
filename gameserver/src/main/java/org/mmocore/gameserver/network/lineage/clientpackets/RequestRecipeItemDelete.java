package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.RecipeHolder;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeBookItemList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.recipe.RecipeComponent;

public class RequestRecipeItemDelete extends L2GameClientPacket {
    private int _recipeId;

    @Override
    protected void readImpl() {
        _recipeId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE) {
            activeChar.sendActionFailed();
            return;
        }
        final Recipe rp = RecipeHolder.getInstance().getRecipeId(_recipeId);
        final RecipeComponent recipeComponent = activeChar.getRecipeComponent();
        if (rp == null || !recipeComponent.findRecipe(_recipeId)) {
            activeChar.sendActionFailed();
            return;
        }
        recipeComponent.unregisterRecipe(_recipeId);
        if (recipeComponent.getDwarvenRecipeBook().contains(rp)) {
            recipeComponent.removeDwarvenRecipe(_recipeId);
        } else {
            recipeComponent.removeCommonRecipe(_recipeId);
        }
        activeChar.sendPacket(new RecipeBookItemList(activeChar, !rp.isCommonRecipe()));
    }
}