package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;

/**
 * @author VISTALL
 */
public class RequestPlayerGamePointDecrease extends SendablePacket {
    private String _account;
    private int _productId;
    private byte _count;
    private long _playerStoredId;
    private int _needGamePoint;

    public RequestPlayerGamePointDecrease(final Player player, final ProductItemTemplate template, final byte count) {
        _account = player.getAccountName();
        _playerStoredId = player.getObjectId();
        _productId = template.getProductId();
        _count = count;
        _needGamePoint = template.getPrice() * count;
    }

    @Override
    public void writeImpl() {
        writeC(0x07);
        writeS(_account);
        writeQ(_playerStoredId);
        writeQ(_needGamePoint);
        writeD(_productId);
        writeC(_count);
    }
}
