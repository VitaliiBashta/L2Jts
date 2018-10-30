package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop.ExBR_BuyProduct;
import org.mmocore.gameserver.network.lineage.serverpackets.StatusUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.premium.ProductItemComponent;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class PlayerGamePointDecrease extends ReceivablePacket {
    private int _ok;
    private int _playerStoredId;
    private long gamePoints;
    // берем не примитивные типы что б не забивало памьять при фейле
    private Integer _productId;
    private Byte _count;

    @Override
    public void readImpl() {
        _ok = readD();
        _playerStoredId = readD();
        if (_ok == 1) {
            _productId = readD();
            _count = (byte) readC();
        }
        gamePoints = readQ();
    }

    @Override
    public void runImpl() {
        final AuthServerCommunication client = getClient();
        if (client == null)
            return;

        final Player player = GameObjectsStorage.getPlayer(_playerStoredId);
        if (player == null)
            return;

        if (_ok == 1) {
            final ProductItemTemplate product = ProductItemHolder.getInstance().getItem(_productId);

            if (product == null || _count == 0) {
                player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_WRONG_PRODUCT));
                return;
            }

            for (final ProductItemComponent $comp : product.getComponents()) {
                player.getInventory().addItem($comp.getItemId(), $comp.getCount() * _count);
            }

            ProductItemHolder.getInstance().productBought(player, product, true);

            player.sendPacket(player.makeStatusUpdate(StatusUpdate.CUR_LOAD));
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_OK));
            player.sendItemList(false);
        } else {
            player.sendPacket(new ExBR_BuyProduct(ExBR_BuyProduct.RESULT_NOT_ENOUGH_POINTS));
        }
        player.getPremiumAccountComponent().setPremiumPoints((int) gamePoints);
    }
}