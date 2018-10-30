package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPutIntensiveResultForVariationMake extends GameServerPacket {
    private final int refinerItemObjId;
    private final int lifestoneItemId;
    private final int gemstoneItemId;
    private final int unk;
    private final long gemstoneCount;

    public ExPutIntensiveResultForVariationMake(final int refinerItemObjId, final int lifeStoneId, final int gemstoneItemId, final long gemstoneCount) {
        this.refinerItemObjId = refinerItemObjId;
        lifestoneItemId = lifeStoneId;
        this.gemstoneItemId = gemstoneItemId;
        this.gemstoneCount = gemstoneCount;
        unk = 1;
    }

    @Override
    protected void writeData() {
        writeD(refinerItemObjId);
        writeD(lifestoneItemId);
        writeD(gemstoneItemId);
        writeQ(gemstoneCount);
        writeD(unk);
    }
}