package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;

/**
 * @author KilRoy
 */
public class ResetAllPlayerReportPoints extends ReceivablePacket {
    private int reportPoints;

    @Override
    protected void readImpl() {
        reportPoints = readD();
    }

    @Override
    protected void runImpl() {
        AccountsDAO.getInstance().resetReportPoints(reportPoints);
    }
}