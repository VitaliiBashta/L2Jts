package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class SendTradeDone extends GameServerPacket {
    public static final GameServerPacket SUCCESS = new SendTradeDone(1);
    public static final GameServerPacket FAIL = new SendTradeDone(0);

    private final int response;

    private SendTradeDone(final int num) {
        response = num;
    }

    @Override
    protected final void writeData() {
        writeD(response);
    }
}