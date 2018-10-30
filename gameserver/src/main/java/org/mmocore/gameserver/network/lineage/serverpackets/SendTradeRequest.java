package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class SendTradeRequest extends GameServerPacket {
    private final int senderId;

    public SendTradeRequest(final int senderId) {
        this.senderId = senderId;
    }

    @Override
    protected final void writeData() {
        writeD(senderId);
    }
}