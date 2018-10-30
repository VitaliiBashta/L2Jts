package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPutEnchantSupportItemResult extends GameServerPacket {
    private final int result;

    public ExPutEnchantSupportItemResult(final int result) {
        this.result = result;
    }

    @Override
    protected void writeData() {
        writeD(result);
    }
}