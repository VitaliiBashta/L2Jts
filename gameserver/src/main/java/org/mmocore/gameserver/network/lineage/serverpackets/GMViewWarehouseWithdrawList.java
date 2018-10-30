package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public class GMViewWarehouseWithdrawList extends GameServerPacket {
    private final ItemInstance[] items;
    private final String charName;
    private final long charAdena;

    public GMViewWarehouseWithdrawList(final Player cha) {
        charName = cha.getName();
        charAdena = cha.getAdena();
        items = cha.getWarehouse().getItems();
    }

    @Override
    protected final void writeData() {
        writeS(charName);
        writeQ(charAdena);
        writeH(items.length);
        for (final ItemInstance temp : items) {
            writeItemInfo(temp);
            writeD(temp.getObjectId());
        }
    }
}