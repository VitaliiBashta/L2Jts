package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPutCommissionResultForVariationMake extends GameServerPacket {
    private final int gemstoneObjId;
    private final int unk1;
    private final int unk3;
    private final long gemstoneCount;
    private final long unk2;

    public ExPutCommissionResultForVariationMake(final int gemstoneObjId, final long count) {
        this.gemstoneObjId = gemstoneObjId;
        unk1 = 1;
        gemstoneCount = count;
        unk2 = 1;
        unk3 = 1;
    }

    @Override
    protected void writeData() {
        writeD(gemstoneObjId);
        writeD(unk1);
        writeQ(gemstoneCount);
        writeQ(unk2);
        writeD(unk3);
    }
}