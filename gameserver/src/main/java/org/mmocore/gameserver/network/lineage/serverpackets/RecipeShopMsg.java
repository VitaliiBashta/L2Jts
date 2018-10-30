package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class RecipeShopMsg extends GameServerPacket {
    private final int objectId;
    private final String storeName;

    public RecipeShopMsg(final Player player) {
        objectId = player.getObjectId();
        storeName = player.getManufactureName();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeS(storeName);
    }
}