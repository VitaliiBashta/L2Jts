package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse.ItemClassComparator;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class PrivateStoreManageListBuy extends GameServerPacket {
    private final int buyerId;
    private final long adena;
    private final List<TradeItem> buyList0;
    private final List<TradeItem> buyList;

    /**
     * Окно управления личным магазином покупки
     *
     * @param buyer
     */
    public PrivateStoreManageListBuy(final Player buyer) {
        buyerId = buyer.getObjectId();
        adena = buyer.getAdena();
        buyList0 = buyer.getBuyList();
        buyList = new ArrayList<>();

        final ItemInstance[] items = buyer.getInventory().getItems();
        ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
        TradeItem bi;
        for (final ItemInstance item : items) {
            if (item.canBeTraded(buyer) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA) {
                buyList.add(bi = new TradeItem(item));
                bi.setObjectId(0);
            }
        }
    }

    @Override
    protected final void writeData() {
        //section 1
        writeD(buyerId);
        writeQ(adena);

        //section2
        writeD(buyList.size());//for potential sells
        for (final TradeItem bi : buyList) {
            writeItemInfo(bi);
            writeQ(bi.getStorePrice());
        }

        //section 3
        writeD(buyList0.size());//count for any items already added for sell
        for (final TradeItem bi : buyList0) {
            writeItemInfo(bi);
            writeQ(bi.getOwnersPrice());
            writeQ(bi.getStorePrice());
            writeQ(bi.getCount());
        }
    }
}