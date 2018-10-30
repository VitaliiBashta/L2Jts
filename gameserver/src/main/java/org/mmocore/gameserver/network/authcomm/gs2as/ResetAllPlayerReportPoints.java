package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

/**
 * @author KilRoy
 */
public class ResetAllPlayerReportPoints extends SendablePacket {
    private int reportPoints;

    public ResetAllPlayerReportPoints(final int reportPoints) {
        this.reportPoints = reportPoints;
    }

    @Override
    protected void writeImpl() {
        writeC(0x09);
        writeD(reportPoints);
    }
}