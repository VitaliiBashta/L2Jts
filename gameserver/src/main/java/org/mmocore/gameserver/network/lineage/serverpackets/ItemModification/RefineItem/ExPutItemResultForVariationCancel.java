package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.VariationUtils;

/**
 * @author VISTALL
 */
public class ExPutItemResultForVariationCancel extends GameServerPacket {
    private final int itemObjectId;
    private final int itemId;
    private final int aug1;
    private final int aug2;
    private final long price;

    public ExPutItemResultForVariationCancel(final ItemInstance item) {
        itemObjectId = item.getObjectId();
        itemId = item.getItemId();
        aug1 = item.getVariation1Id();
        aug2 = item.getVariation2Id();
        price = VariationUtils.getRemovePrice(item);
    }

    @Override
    protected void writeData() {
        writeD(itemObjectId);
        writeD(itemId);
        writeD(aug1);
        writeD(aug2);
        writeQ(price);
        writeD(0x01);
    }
}