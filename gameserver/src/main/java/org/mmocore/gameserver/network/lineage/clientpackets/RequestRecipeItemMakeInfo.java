package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.RecipeHolder;
import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.network.lineage.serverpackets.RecipeItemMakeInfo;
import org.mmocore.gameserver.object.Player;

public class RequestRecipeItemMakeInfo extends L2GameClientPacket {
    private int _id;

    /**
     * packet type id 0xB7
     * format:		cd
     */
    @Override
    protected void readImpl() {
        _id = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Recipe recipe = RecipeHolder.getInstance().getRecipeId(_id);
        if (recipe == null) {
            activeChar.sendActionFailed();
            return;
        }

        sendPacket(new RecipeItemMakeInfo(activeChar, recipe, 0xffffffff));
    }
}