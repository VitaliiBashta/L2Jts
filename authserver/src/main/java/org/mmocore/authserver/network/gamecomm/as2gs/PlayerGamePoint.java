package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

/**
 * Author: VISTALL
 * Company: J Develop Station
 * Date:  12:54:15/15.09.2010
 */
public class PlayerGamePoint extends SendablePacket {
    private int _gamePoint, _playerStoredId;

    public PlayerGamePoint(long gamePoint, long playerStoredId) {
        _gamePoint = (int) gamePoint;
        _playerStoredId = (int) playerStoredId;
    }

    @Override
    public void writeImpl() {
        writeC(0x06);
        writeD(_gamePoint);
        writeD(_playerStoredId);
    }
}
