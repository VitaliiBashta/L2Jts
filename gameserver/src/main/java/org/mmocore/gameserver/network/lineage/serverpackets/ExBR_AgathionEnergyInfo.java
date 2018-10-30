package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author VISTALL
 */
public class ExBR_AgathionEnergyInfo extends GameServerPacket {
    private final int size;
    private ItemInstance[] itemList = null;

    public ExBR_AgathionEnergyInfo(final int size, final ItemInstance... item) {
        itemList = item;
        this.size = size;
    }

    @Override
    protected void writeData() {
        writeD(size);
        for (final ItemInstance item : itemList) {
            if (item.getAgathionMaxEnergy() <= 0) {
                continue;
            }
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeD(0x200000);
            writeD(item.getAgathionEnergy());//current energy
            writeD(item.getAgathionMaxEnergy());   //max energy
        }
    }
}