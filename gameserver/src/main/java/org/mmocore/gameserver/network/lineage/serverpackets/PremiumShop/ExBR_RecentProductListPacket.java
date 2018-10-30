package org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;

import java.util.Collection;
import java.util.Collections;

public class ExBR_RecentProductListPacket extends GameServerPacket {
    private Collection<ProductItemTemplate> items = Collections.emptyList();

    public ExBR_RecentProductListPacket(final Player player) {
        items = player.getPremiumAccountComponent().getBoughtProducts();
    }

    @Override
    protected void writeData() {
        writeD(items.size());

        for (final ProductItemTemplate template : items) {
            writeD(template.getProductId()); // product id
            writeH(template.getCategory()); // category 1 - enchant 2 - supplies 3 - decoration 4 - package 5 - other
            writeD(template.getPrice()); // price
            writeD(template.getEventFlag().ordinal());// show tab 2-th group 1 - event 2 - best 3 - event & best
            writeD((int) template.getStartSaleDate()); // start sale
            writeD((int) template.getEndSaleDate()); // end sale
            writeC(template.getDayWeek()); // day week
            writeC(template.getStartSaleHour()); // start hour
            writeC(template.getStartSaleMin()); // start min
            writeC(template.getEndSaleHour()); // end hour
            writeC(template.getEndSaleMin()); // end min
            writeD(template.getCurrentStock()); // current stock
            writeD(template.getMaxStock()); // max stock
        }
    }
}