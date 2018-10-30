package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

/**
 * Author: VISTALL
 * Company: J Develop Station
 * Date:  16:13:31/15.09.2010
 */
public class PlayerGamePointDecrease extends SendablePacket {
    private int _ok;
    private int _playerStoredId;
    private long gamePoints;
    // берем не примитивные типы что б не забивало памьять при фейле
    private Integer _productId;
    private Byte _count;

    public PlayerGamePointDecrease(long playerStoredId, long gamePoints) {
        _ok = 0;
        _playerStoredId = (int) playerStoredId;
        this.gamePoints = gamePoints;
    }

    public PlayerGamePointDecrease(long playerStoredId, int productId, byte count, long gamePoints) {
        _ok = 1;
        _playerStoredId = (int) playerStoredId;
        _productId = productId;
        _count = count;
        this.gamePoints = gamePoints;
    }

    @Override
    public void writeImpl() {
        writeC(0x07);
        writeD(_ok);
        writeD(_playerStoredId);
        if (_ok == 1) {
            writeD(_productId);
            writeC(_count);
        }
        writeQ(gamePoints);
    }
}
