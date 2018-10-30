package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.TradeItem;

import java.util.List;

public class PrivateStoreListSell extends GameServerPacket {
    private final int sellerId;
    private final long adena;
    private final boolean pkg;
    private final List<TradeItem> sellList;

    /**
     * Список вещей в личном магазине продажи, показываемый покупателю
     *
     * @param buyer
     * @param seller
     */
    public PrivateStoreListSell(final Player buyer, final Player seller) {
        sellerId = seller.getObjectId();
        adena = buyer.getAdena();
        pkg = seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
        sellList = seller.getSellList();
    }

    @Override
    protected final void writeData() {
        writeD(sellerId);
        writeD(pkg ? 1 : 0);
        writeQ(adena);
        writeD(sellList.size());
        for (final TradeItem si : sellList) {
            writeItemInfo(si);
            writeQ(si.getOwnersPrice());
            writeQ(si.getStorePrice());
        }
    }
}