package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class PlayerInGame extends SendablePacket {
    private final String account;

    public PlayerInGame(final String account) {
        this.account = account;
    }

    @Override
    protected void writeImpl() {
        writeC(0x03);
        writeS(account);
    }
}
