package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class RequestPlayerGamePointIncrease extends SendablePacket {
    private String account;
    private long pointCount;
    private long playerStoredId;
    private int decrease;

    public RequestPlayerGamePointIncrease(final Player player, final long pointCount, final boolean decrease) {
        account = player.getAccountName();
        playerStoredId = player.getObjectId();
        this.pointCount = pointCount;
        this.decrease = decrease ? 1 : 0;
    }

    @Override
    public void writeImpl() {
        writeC(0x14);
        writeS(account);
        writeQ(playerStoredId);
        writeQ(pointCount);
        writeD(decrease);
    }
}