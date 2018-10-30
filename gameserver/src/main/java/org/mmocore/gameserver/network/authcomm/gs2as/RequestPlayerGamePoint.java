package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestPlayerGamePoint extends SendablePacket {
    private final String _account;
    private final long _playerStoredId;

    public RequestPlayerGamePoint(final Player player) {
        _account = player.getAccountName();
        _playerStoredId = player.getObjectId();
    }

    @Override
    public void writeImpl() {
        writeC(0x06);
        writeS(_account);
        writeQ(_playerStoredId);
    }
}
