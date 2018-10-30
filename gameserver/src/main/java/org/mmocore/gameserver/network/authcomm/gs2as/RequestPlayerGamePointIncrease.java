package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class RequestPlayerGamePointIncrease extends SendablePacket {
    private final String account;
    private final long pointCount;
    private final long playerStoredId;
    private final int decrease;

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