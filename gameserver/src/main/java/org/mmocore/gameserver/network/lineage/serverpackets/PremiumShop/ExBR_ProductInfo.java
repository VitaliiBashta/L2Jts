package org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop;

import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.premium.ProductItemComponent;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;

public class ExBR_ProductInfo extends GameServerPacket {
    private final ProductItemTemplate product;

    public ExBR_ProductInfo(final int productId) {
        product = ProductItemHolder.getInstance().getItem(productId);
    }

    @Override
    protected void writeData() {
        if (product == null)
            return;

        writeD(product.getProductId()); // product id
        writeD(product.getPrice()); // price points
        writeD(product.getComponents().size()); // size

        for (final ProductItemComponent com : product.getComponents()) {
            writeD(com.getItemId()); // item id
            writeD(com.getCount()); // count
            writeD(com.getWeight()); // weight
            writeD(com.isDropable() ? 1 : 0); // 0 - dont drop/trade
        }
    }
}