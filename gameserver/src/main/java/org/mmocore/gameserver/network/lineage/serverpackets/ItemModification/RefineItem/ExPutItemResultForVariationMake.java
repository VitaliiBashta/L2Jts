package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPutItemResultForVariationMake extends GameServerPacket {
    private final int itemObjId;
    private final int unk1;
    private final int unk2;

    public ExPutItemResultForVariationMake(final int itemObjId) {
        this.itemObjId = itemObjId;
        unk1 = 1;
        unk2 = 1;
    }

    @Override
    protected void writeData() {
        writeD(itemObjId);
        writeD(unk1);
        writeD(unk2);
    }
}