package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.PremiumItem;

import java.util.Map;

/**
 * @author Gnacik
 * @corrected by n0nam3
 */
public class ExGetPremiumItemList extends GameServerPacket {
    private final int objectId;
    private final Map<Integer, PremiumItem> list;

    public ExGetPremiumItemList(final Player activeChar) {
        objectId = activeChar.getObjectId();
        list = activeChar.getPremiumAccountComponent().getPremiumItemList();
    }

    @Override
    protected void writeData() {
        if (!list.isEmpty()) {
            writeD(list.size());
            for (final Map.Entry<Integer, PremiumItem> entry : list.entrySet()) {
                writeD(entry.getKey());
                writeD(objectId);
                writeD(entry.getValue().getItemId());
                writeQ(entry.getValue().getCount());
                writeD(0);
                writeS(entry.getValue().getSender());
            }
        }
    }

}