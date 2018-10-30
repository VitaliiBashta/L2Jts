package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.mmocore.authserver.network.gamecomm.as2gs.PlayerGamePointDecrease;

/**
 * @author VISTALL
 * @modified KilRoy
 */
public class RequestPlayerGamePointDecrease extends ReceivablePacket {

    private String _account;
    private long _playerStoredId;
    private long _needGamePoint;
    private int _productId;
    private byte _count;

    @Override
    public void readImpl() {
        _account = readS();
        _playerStoredId = readQ();
        _needGamePoint = readQ();
        _productId = readD();
        _count = (byte) readC();
    }

    @Override
    public void runImpl() {
        GameServer client = getClient();
        if (client == null)
            return;

        long gamePoints = AccountsDAO.getInstance().requestGamePoints(_account);
        if (gamePoints >= _needGamePoint) {
            AccountsDAO.getInstance().updateGamePoints(_account, gamePoints - _needGamePoint);
            final long points = AccountsDAO.getInstance().requestGamePoints(_account);
            client.sendPacket(new PlayerGamePointDecrease(_playerStoredId, _productId, _count, points));
        } else
            client.sendPacket(new PlayerGamePointDecrease(_playerStoredId, gamePoints));
    }
}