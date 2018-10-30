package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.ItemClassComparator;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.WarehouseType;

import java.util.ArrayList;
import java.util.List;


public class WareHouseDepositList extends GameServerPacket {
    private final int whtype;
    private final long adena;
    private final List<ItemInfo> itemList;

    public WareHouseDepositList(final Player cha, final WarehouseType whtype) {
        this.whtype = whtype.ordinal();
        adena = cha.getAdena();

        final ItemInstance[] items = cha.getInventory().getItems();
        ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
        itemList = new ArrayList<>(items.length);
        for (final ItemInstance item : items) {
            if (item.canBeStored(cha, this.whtype == 1)) {
                itemList.add(new ItemInfo(item));
            }
        }
    }

    @Override
    protected final void writeData() {
        writeH(whtype);
        writeQ(adena);
        writeH(itemList.size());
        for (final ItemInfo item : itemList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }
    }
}