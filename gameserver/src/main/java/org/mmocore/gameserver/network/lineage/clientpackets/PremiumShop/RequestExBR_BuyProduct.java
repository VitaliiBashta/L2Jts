package org.mmocore.gameserver.network.lineage.clientpackets.PremiumShop;

import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointDecrease;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop.ExBR_BuyProduct;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.premium.ProductItemComponent;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;

public class RequestExBR_BuyProduct extends L2GameClientPacket {
    private int _productId;
    private int _count;

    @Override
    protected void readImpl() {
        _productId = readD();
        _count = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (player.isOutOfControl() || player.isActionsDisabled()) {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_USER_STATE));
            return;
        }

        if (_count > 99 || _count < 0)
            return;

        final ProductItemTemplate product = ProductItemHolder.getInstance().getItem(_productId);
        if (product == null) {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
            return;
        }

        if (!ProductItemHolder.getInstance().calcStartEndTime(product.getProductId())) {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_SALE_PERIOD_ENDED));
            return;
        }

        final long totalPoints = product.getPrice() * _count;

        if (totalPoints < 0) {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
            return;
        }

        if (totalPoints > player.getPremiumAccountComponent().getPremiumPoints()) {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_ENOUGH_POINTS));
            return;
        }

        int totalWeight = 0;
        for (final ProductItemComponent com : product.getComponents()) {
            totalWeight += com.getWeight();
        }
        totalWeight *= _count; //увеличиваем вес согласно количеству

        int totalCount = 0;

        for (final ProductItemComponent com : product.getComponents()) {
            final ItemTemplate item = ItemTemplateHolder.getInstance().getTemplate(com.getItemId());

            if (item == null) {
                player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT_ITEM));
                return; //what
            }

            totalCount += item.isStackable() ? 1 : com.getCount() * _count;
        }

        if (!player.getInventory().validateCapacity(totalCount) || !player.getInventory().validateWeight(totalWeight)) {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_INVENTORY_FULL));
            return;
        }

        AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointDecrease(player, product, (byte) _count));
    }
}