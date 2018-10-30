package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.manor.CropProcure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellListProcure extends GameServerPacket {
    private final long money;
    private final Map<ItemInstance, Long> sellList = new HashMap<>();

    public SellListProcure(final Player player, final int castleId) {
        money = player.getAdena();
        int castle = castleId;
        List<CropProcure> procureList = ResidenceHolder.getInstance().getResidence(Castle.class, castle).getCropProcure(0);
        for (final CropProcure c : procureList) {
            final ItemInstance item = player.getInventory().getItemByItemId(c.getId());
            if (item != null && c.getAmount() > 0) {
                sellList.put(item, c.getAmount());
            }
        }
    }

    @Override
    protected final void writeData() {
        writeQ(money);
        writeD(0x00); // lease ?
        writeH(sellList.size()); // list size

        for (final ItemInstance item : sellList.keySet()) {
            writeH(item.getTemplate().getType1());
            writeD(item.getObjectId());
            writeD(item.getItemId());
            writeQ(sellList.get(item));
            writeH(item.getTemplate().getType2ForPackets());
            writeH(0); // size of [dhhh]
            writeQ(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }
}