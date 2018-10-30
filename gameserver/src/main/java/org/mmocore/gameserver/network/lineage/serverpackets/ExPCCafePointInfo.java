package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * Format: ch ddcdc
 * <p/>
 * Args: player, points to add, type of period (default 1), type of points (1-double, 2-integer), time left to the end of period
 */
public class ExPCCafePointInfo extends GameServerPacket {
    private final int mAddPoint;
    private final int mPeriodType;
    private final int pointType;
    private final int pcBangPoints;
    private final int remainTime;

    public ExPCCafePointInfo(final Player player, final int mAddPoint, final int mPeriodType, final int pointType, final int remainTime) {
        pcBangPoints = player.getPremiumAccountComponent().getPcBangPoints();
        this.mAddPoint = mAddPoint;
        this.mPeriodType = mPeriodType;
        this.pointType = pointType;
        this.remainTime = remainTime;
    }

    @Override
    protected final void writeData() {
        writeD(pcBangPoints);
        writeD(mAddPoint);
        writeC(mPeriodType);
        writeD(remainTime);
        writeC(pointType);
    }
}