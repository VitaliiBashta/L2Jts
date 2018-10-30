package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.recipe.Recipe;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * format ddddd
 */
public class RecipeItemMakeInfo extends GameServerPacket {
    private final int id;
    private final int isCommon;
    private final int status;
    private final int curMP;
    private final int maxMP;

    public RecipeItemMakeInfo(final Player player, final Recipe recipe, final int status) {
        id = recipe.getId();
        isCommon = recipe.getIsCommonRecipe();
        this.status = status;
        curMP = (int) player.getCurrentMp();
        maxMP = player.getMaxMp();
        //
    }

    @Override
    protected final void writeData() {
        writeD(id); //ID рецепта
        writeD(isCommon);
        writeD(curMP);
        writeD(maxMP);
        writeD(status); //итог крафта; 0xFFFFFFFF нет статуса, 0 удача, 1 провал
    }
}