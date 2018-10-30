package org.mmocore.gameserver.network.lineage.serverpackets.GoodsInventory;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 * @date 11:33/03.07.2011
 */
public class ExGoodsInventoryChangedNotify extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExGoodsInventoryChangedNotify();

    @Override
    protected void writeData() {
    }
}