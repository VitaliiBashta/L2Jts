package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.RefineItem;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Открывает окно аугмента, название от фонаря.
 */
public class ExShowVariationMakeWindow extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExShowVariationMakeWindow();

    @Override
    protected final void writeData() {
    }
}