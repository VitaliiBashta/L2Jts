package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 20:46/16.05.2011
 */
public class PackageSendableList extends GameServerPacket {
    private final int targetObjectId;
    private final long adena;
    private final List<ItemInfo> itemList;

    public PackageSendableList(final int objectId, final Player cha) {
        adena = cha.getAdena();
        targetObjectId = objectId;

        final ItemInstance[] items = cha.getInventory().getItems();
        ArrayUtils.eqSort(items, Warehouse.ItemClassComparator.getInstance());
        itemList = new ArrayList<>(items.length);
        for (final ItemInstance item : items) {
            if (item.getTemplate().isFreightable()) {
                itemList.add(new ItemInfo(item));
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(targetObjectId);
        writeQ(adena);
        writeD(itemList.size());
        for (final ItemInfo item : itemList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }
    }
}
