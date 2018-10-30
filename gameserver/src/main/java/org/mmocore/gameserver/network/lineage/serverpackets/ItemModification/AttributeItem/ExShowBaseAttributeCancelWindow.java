package org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem;

import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SYS
 */
public class ExShowBaseAttributeCancelWindow extends GameServerPacket {
    private final List<ItemInstance> items = new ArrayList<>();

    public ExShowBaseAttributeCancelWindow(final Player activeChar) {
        for (final ItemInstance item : activeChar.getInventory().getItems()) {
            if (item.getAttributeElement() == Element.NONE || !item.canBeEnchanted() || getAttributeRemovePrice(item) == 0) {
                continue;
            }
            items.add(item);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public static long getAttributeRemovePrice(final ItemInstance item) {
        switch (item.getCrystalType()) {
            case S:
                return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 50000 : 40000;
            case S80:
                return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 100000 : 80000;
            case S84:
                return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 200000 : 160000;
        }
        return 0;
    }

    @Override
    protected final void writeData() {
        writeD(items.size());
        for (final ItemInstance item : items) {
            writeD(item.getObjectId());
            writeQ(getAttributeRemovePrice(item));
        }
    }
}