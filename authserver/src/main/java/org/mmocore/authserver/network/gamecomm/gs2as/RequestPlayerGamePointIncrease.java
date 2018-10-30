package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.mmocore.authserver.network.gamecomm.as2gs.PlayerGamePoint;

/**
 * @author KilRoy
 */
public class RequestPlayerGamePointIncrease extends ReceivablePacket {
    private String account;
    private long playerStoredId;
    private long pointCount;
    private boolean decrease;

    @Override
    public void readImpl() {
        account = readS();
        playerStoredId = readQ();
        pointCount = readQ();
        decrease = readD() == 1;
    }

    @Override
    public void runImpl() {
        GameServer client = getClient();
        if (client == null)
            return;

        final long oldGamePoints = AccountsDAO.getInstance().requestGamePoints(account);
        Long newGamePoint;
        if (decrease) {
            newGamePoint = oldGamePoints - pointCount;
            if (newGamePoint <= -1)
                newGamePoint = 0L;
        } else
            newGamePoint = oldGamePoints + pointCount;
        AccountsDAO.getInstance().updateGamePoints(account, newGamePoint);
        client.sendPacket(new PlayerGamePoint(newGamePoint, playerStoredId));
    }
}