package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.TradeItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: c ddh[hdddhhd]
 * c - id (0xE8)
 * <p/>
 * d - money
 * d - manor id
 * h - size
 * [
 * h - item type 1
 * d - object id
 * d - item id
 * d - count
 * h - item type 2
 * h
 * d - price
 * ]
 */
public final class BuyListSeed extends GameServerPacket {
    private final int manorId;
    private final long money;
    private List<TradeItem> list = new ArrayList<>();

    public BuyListSeed(final List<TradeItem> list, final int manorId, final long currentMoney) {
        money = currentMoney;
        this.manorId = manorId;
        this.list = list;
    }

    @Override
    protected final void writeData() {
        writeQ(money); // current money
        writeD(manorId); // manor id
        writeH(list.size()); // list length
        for (final TradeItem item : list) {
            writeItemInfo(item);
            writeQ(item.getOwnersPrice());
        }
    }
}