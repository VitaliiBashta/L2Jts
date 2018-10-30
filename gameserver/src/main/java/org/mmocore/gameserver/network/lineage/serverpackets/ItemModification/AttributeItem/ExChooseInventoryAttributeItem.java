package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem;

import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneManager;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class ExChooseInventoryAttributeItem extends GameServerPacket {
    private final int itemId;
    private final int stoneLvl;
    private int[] att;

    public ExChooseInventoryAttributeItem(final ItemInstance item) {
        itemId = item.getItemId();
        att = new int[6];
        AttributeStoneInfo asi = AttributeStoneManager.getStoneInfo(itemId);
        att[asi.getElement().getId()] = 1;
        stoneLvl = asi.getStoneLevel();
    }

    @Override
    protected final void writeData() {
        writeD(itemId);
        for (int i : att)
            writeD(i);
        writeD(stoneLvl); //max enchant lvl
    }
}