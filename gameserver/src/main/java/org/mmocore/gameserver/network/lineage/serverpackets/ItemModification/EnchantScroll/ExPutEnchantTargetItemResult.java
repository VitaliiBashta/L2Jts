package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPutEnchantTargetItemResult extends GameServerPacket {
    public static final GameServerPacket FAIL = new ExPutEnchantTargetItemResult(0);
    public static final GameServerPacket SUCCESS = new ExPutEnchantTargetItemResult(1);

    private final int result;

    public ExPutEnchantTargetItemResult(final int result) {
        this.result = result;
    }

    @Override
    protected void writeData() {
        writeD(result);
    }
}