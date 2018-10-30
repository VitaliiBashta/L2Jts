package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ManufactureItem;

import java.util.List;


public class RecipeShopSellList extends GameServerPacket {
    private final int objId;
    private final int curMp;
    private final int maxMp;
    private final long adena;
    private final List<ManufactureItem> createList;

    public RecipeShopSellList(final Player buyer, final Player manufacturer) {
        objId = manufacturer.getObjectId();
        curMp = (int) manufacturer.getCurrentMp();
        maxMp = manufacturer.getMaxMp();
        adena = buyer.getAdena();
        createList = manufacturer.getCreateList();
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeD(curMp);//Creator's MP
        writeD(maxMp);//Creator's MP
        writeQ(adena);
        writeD(createList.size());
        for (final ManufactureItem mi : createList) {
            writeD(mi.getRecipeId());
            writeD(0x00); //unknown
            writeQ(mi.getCost());
        }
    }
}