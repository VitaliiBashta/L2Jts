package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.TradeItem;

import java.util.ArrayList;
import java.util.List;

public class PrivateStoreListBuy extends GameServerPacket {
    private final int buyerId;
    private final long adena;
    private final List<TradeItem> sellList;

    /**
     * Список вещей в личном магазине покупки, показываемый продающему
     *
     * @param seller
     * @param buyer
     */
    public PrivateStoreListBuy(final Player seller, final Player buyer) {
        adena = seller.getAdena();
        buyerId = buyer.getObjectId();
        sellList = new ArrayList<>();

        final List<TradeItem> buyList = buyer.getBuyList();
        final ItemInstance[] items = seller.getInventory().getItems();

        for (final TradeItem bi : buyList) {
            TradeItem si = null;
            for (final ItemInstance item : items) {
                if (item.getItemId() == bi.getItemId() && item.canBeTraded(seller)) {
                    if (bi.getEnchantLevel() > 0 && item.getEnchantLevel() < bi.getEnchantLevel())
                        continue;
                    si = new TradeItem(item);
                    sellList.add(si);
                    si.setOwnersPrice(bi.getOwnersPrice());
                    si.setCount(bi.getCount());
                    si.setCurrentValue(Math.min(bi.getCount(), item.getCount()));
                }
            }
            if (si == null) {
                si = new TradeItem();
                si.setItemId(bi.getItemId());
                si.setOwnersPrice(bi.getOwnersPrice());
                si.setCount(bi.getCount());
                si.setEnchantLevel(bi.getEnchantLevel());
                si.setCurrentValue(0);
                sellList.add(si);
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(buyerId);
        writeQ(adena);
        writeD(sellList.size());
        for (final TradeItem si : sellList) {
            writeItemInfo(si, si.getCurrentValue());
            writeD(si.getObjectId());
            writeQ(si.getOwnersPrice());
            writeQ(si.getStorePrice());
            writeQ(si.getCount()); // maximum possible tradecount
        }
    }
}