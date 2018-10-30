package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Ответ на запрос создания нового письма.
 * Отсылается при получении {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExPostItemList}
 * Содержит список вещей, которые можно приложить к письму.
 */
public class ExReplyPostItemList extends GameServerPacket {
    private final List<ItemInfo> itemsList = new ArrayList<>();

    public ExReplyPostItemList(final Player activeChar) {
        final ItemInstance[] items = activeChar.getInventory().getItems();
        for (final ItemInstance item : items) {
            if (item.canBeTraded(activeChar)) {
                itemsList.add(new ItemInfo(item));
            }
        }
    }

    @Override
    protected void writeData() {
        writeD(itemsList.size());
        for (final ItemInfo item : itemsList) {
            writeItemInfo(item);
        }
    }
}