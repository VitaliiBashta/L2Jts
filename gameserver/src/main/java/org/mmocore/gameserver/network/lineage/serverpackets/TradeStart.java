package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public class TradeStart extends GameServerPacket {
    private final List<ItemInfo> tradelist = new ArrayList<>();
    private final int targetId;

    public TradeStart(final Player player, final Player target) {
        targetId = target.getObjectId();

        final ItemInstance[] items = player.getInventory().getItems();
        for (final ItemInstance item : items) {
            if (item.canBeTraded(player)) {
                tradelist.add(new ItemInfo(item));
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(targetId);
        writeH(tradelist.size());
        for (final ItemInfo item : tradelist) {
            writeItemInfo(item);
        }
    }
}