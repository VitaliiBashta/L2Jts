package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExBuySellList extends L2GameServerPacket {
    private final List<TradeItem> sellList;
    private final List<TradeItem> refundList;
    private final int done;

    public ExBuySellList(final Player activeChar, final boolean done) {
        this.done = done ? 1 : 0;
        if (done) {
            refundList = Collections.emptyList();
            sellList = Collections.emptyList();
        } else {
            ItemInstance[] items = activeChar.getRefund().getItems();
            refundList = new ArrayList<>(items.length);
            for (final ItemInstance item : items) {
                refundList.add(new TradeItem(item));
            }

            items = activeChar.getInventory().getItems();
            sellList = new ArrayList<>(items.length);
            for (final ItemInstance item : items) {
                if (item.canBeSold(activeChar)) {
                    sellList.add(new TradeItem(item));
                }
            }
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0xB7);
        writeD(0x01);
        writeH(sellList.size());
        for (final TradeItem item : sellList) {
            writeItemInfo(item);
            writeQ(item.getReferencePrice() / 2);
        }
        writeH(refundList.size());
        for (final TradeItem item : refundList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
            writeQ(item.getCount() * item.getReferencePrice() / 2);
        }
        writeC(done);
    }
}