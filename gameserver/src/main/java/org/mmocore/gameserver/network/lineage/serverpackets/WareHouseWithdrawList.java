package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInfo;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.ItemClassComparator;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.WarehouseType;
import org.mmocore.gameserver.templates.item.ItemTemplate.ItemClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WareHouseWithdrawList extends GameServerPacket {
    private final long adena;
    private final int type;
    private List<ItemInfo> itemList = new ArrayList<>();

    public WareHouseWithdrawList(final Player player, final WarehouseType type, final ItemClass clss) {
        adena = player.getAdena();
        this.type = type.ordinal();

        final ItemInstance[] items;
        switch (type) {
            case PRIVATE:
                items = player.getWarehouse().getItems(clss);
                break;
            case FREIGHT:
                items = player.getFreight().getItems(clss);
                break;
            case CLAN:
            case CASTLE:
                items = player.getClan().getWarehouse().getItems(clss);
                break;
            default:
                itemList = Collections.emptyList();
                return;
        }

        itemList = new ArrayList<>(items.length);
        ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
        for (final ItemInstance item : items) {
            itemList.add(new ItemInfo(item));
        }
    }

    @Override
    protected final void writeData() {
        writeH(type);
        writeQ(adena);
        writeH(itemList.size());
        for (final ItemInfo item : itemList) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }
    }
}