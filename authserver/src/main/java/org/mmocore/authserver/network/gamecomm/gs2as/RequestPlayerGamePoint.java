package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.mmocore.authserver.network.gamecomm.as2gs.PlayerGamePoint;

/**
 * @author VISTALL
 * @modified KilRoy
 */
public class RequestPlayerGamePoint extends ReceivablePacket {

    private String _account;
    private long _playerStoredId;

    @Override
    public void readImpl() {
        _account = readS();
        _playerStoredId = readQ();
    }

    @Override
    public void runImpl() {
        GameServer client = getClient();
        if (client == null)
            return;

        long points = AccountsDAO.getInstance().requestGamePoints(_account);

        client.sendPacket(new PlayerGamePoint(points, _playerStoredId));
    }
}