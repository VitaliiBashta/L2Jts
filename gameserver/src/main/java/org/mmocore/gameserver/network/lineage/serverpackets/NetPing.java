package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author KilRoy
 */
public class NetPing extends GameServerPacket {
    private final int clientId;

    public NetPing(int clientId) {
        this.clientId = clientId;
    }

    @Override
    protected void writeData() {
        writeD(clientId);
    }
}