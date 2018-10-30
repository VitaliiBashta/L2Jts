package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author SYS
 */
public class ExAttributeEnchantResult extends GameServerPacket {
    private final int result;

    public ExAttributeEnchantResult(final int unknown) {
        result = unknown;
    }

    @Override
    protected final void writeData() {
        writeD(result);
    }
}