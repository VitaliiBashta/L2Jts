package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class RequestReportPoints extends SendablePacket {
    private final String account;
    private final boolean save;
    private final int points;

    public RequestReportPoints(final String account, final boolean save, final int points) {
        this.account = account;
        this.save = save;
        this.points = points;

    }

    @Override
    protected void writeImpl() {
        writeC(0x08);
        writeS(account);
        writeD(save ? 2 : 1);
        writeD(points);
    }
}