package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExVariationResult extends GameServerPacket {
    private final int stat12;
    private final int stat34;
    private final int unk3;

    public ExVariationResult(final int stat12, final int stat34, final int unk3) {
        this.stat12 = stat12;
        this.stat34 = stat34;
        this.unk3 = unk3;
    }

    @Override
    protected void writeData() {
        writeD(stat12);
        writeD(stat34);
        writeD(unk3);
    }
}