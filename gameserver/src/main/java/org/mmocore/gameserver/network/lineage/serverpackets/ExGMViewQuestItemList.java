package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author VISTALL
 * @date 4:20/06.05.2011
 */
public class ExGMViewQuestItemList extends GameServerPacket {
    private final int size;
    private final ItemInstance[] items;

    private final int limit;
    private final String name;

    public ExGMViewQuestItemList(final Player player, final ItemInstance[] items, final int size) {
        this.items = items;
        this.size = size;
        name = player.getName();
        limit = OtherConfig.INVENTORY_BASE_QUEST;
    }

    @Override
    protected final void writeData() {
        writeS(name);
        writeD(limit);
        writeH(size);
        for (final ItemInstance temp : items) {
            if (temp.getTemplate().isQuest()) {
                writeItemInfo(temp);
            }
        }
    }
}
