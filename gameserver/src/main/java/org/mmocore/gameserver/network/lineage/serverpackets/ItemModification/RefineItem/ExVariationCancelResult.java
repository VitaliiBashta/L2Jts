package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExVariationCancelResult extends GameServerPacket {
    private final int closeWindow;
    private final int unk1;

    public ExVariationCancelResult(final int result) {
        closeWindow = 1;
        unk1 = result;
    }

    @Override
    protected void writeData() {
        writeD(unk1);
        writeD(closeWindow);
    }
}