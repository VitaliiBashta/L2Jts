package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class ChangeAccessLevel extends SendablePacket {
    private final String account;
    private final int level;
    private final int banExpire;

    public ChangeAccessLevel(final String account, final int level, final int banExpire) {
        this.account = account;
        this.level = level;
        this.banExpire = banExpire;
    }

    protected void writeImpl() {
        writeC(0x11);
        writeS(account);
        writeD(level);
        writeD(banExpire);
    }
}
