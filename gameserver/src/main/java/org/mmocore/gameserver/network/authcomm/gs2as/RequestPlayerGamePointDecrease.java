package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;

/**
 * @author VISTALL
 */
public class RequestPlayerGamePointDecrease extends SendablePacket {
    private final String _account;
    private final int _productId;
    private final byte _count;
    private final long _playerStoredId;
    private final int _needGamePoint;

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
