package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.mmocore.authserver.network.gamecomm.as2gs.ReportPointSend;

public class RequestReportPoint extends ReceivablePacket {
    private String accounts;
    private int save;
    private int points;

    @Override
    protected void readImpl() {
        accounts = readS();
        save = readD();
        points = readD();
    }

    @Override
    protected void runImpl() {
        Account acc = new Account(accounts);
        int point;
        if (save == 1) {
            if (acc.getReportPoints() == 0)
                point = AccountsDAO.getInstance().requestReportPoints(accounts);
            else
                point = acc.getReportPoints();
            getGameServer().sendPacket(new ReportPointSend(accounts, point));
        } else if (save == 2) {
            acc.setReportPoints(points);
            AccountsDAO.getInstance().updateReportPoints(accounts, points);
        }
    }
}