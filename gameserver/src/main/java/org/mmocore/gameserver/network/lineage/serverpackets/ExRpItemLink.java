package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInfo;

/**
 * ddQhdhhhhhdhhhhhhhh - Gracia Final
 */
public class ExRpItemLink extends GameServerPacket {
    private final ItemInfo item;

    public ExRpItemLink(final ItemInfo item) {
        this.item = item;
    }

    @Override
    protected final void writeData() {
        writeItemInfo(item);
    }
}