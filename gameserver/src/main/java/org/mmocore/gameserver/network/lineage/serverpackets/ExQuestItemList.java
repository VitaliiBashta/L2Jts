package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.LockType;

/**
 * @author VISTALL
 * @date 1:02/23.02.2011
 */
public class ExQuestItemList extends GameServerPacket {
    private final int size;
    private final ItemInstance[] items;

    private final LockType lockType;
    private final int[] lockItems;

    public ExQuestItemList(final int size, final ItemInstance[] t, final LockType lockType, final int[] lockItems) {
        this.size = size;
        items = t;
        this.lockType = lockType;
        this.lockItems = lockItems;
    }

    @Override
    protected void writeData() {
        writeH(size);

        for (final ItemInstance temp : items) {
            if (!temp.getTemplate().isQuest()) {
                continue;
            }

            writeItemInfo(temp);
        }

        writeH(lockItems.length);
        if (lockItems.length > 0) {
            writeC(lockType.ordinal());
            for (final int i : lockItems) {
                writeD(i);
            }
        }
    }
}
