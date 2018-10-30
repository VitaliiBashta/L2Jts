package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ChooseInventoryItem extends GameServerPacket {
    private final int ItemID;

    public ChooseInventoryItem(final int id) {
        ItemID = id;
    }

    @Override
    protected final void writeData() {
        writeD(ItemID);
    }
}