package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * dddddQ
 */
public class RecipeShopItemInfo extends GameServerPacket {
    private final int recipeId;
    private final int shopId;
    private final int curMp;
    private final int maxMp;
    private final long price;
    private int success = 0xFFFFFFFF;

    public RecipeShopItemInfo(final Player activeChar, final Player manufacturer, final int recipeId, final long price, final int success) {
        this.recipeId = recipeId;
        shopId = manufacturer.getObjectId();
        this.price = price;
        this.success = success;
        curMp = (int) manufacturer.getCurrentMp();
        maxMp = manufacturer.getMaxMp();
    }

    @Override
    protected final void writeData() {
        writeD(shopId);
        writeD(recipeId);
        writeD(curMp);
        writeD(maxMp);
        writeD(success);
        writeQ(price);
    }
}