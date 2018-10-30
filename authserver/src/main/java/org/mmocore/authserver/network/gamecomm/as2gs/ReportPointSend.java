package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

public class ReportPointSend extends SendablePacket {
    private String account;
    private int points;

    public ReportPointSend(String account, int points) {
        this.account = account;
        this.points = points;
    }

    @Override
    protected void writeImpl() {
        writeC(0x08);
        writeS(account);
        writeD(points);
    }
}