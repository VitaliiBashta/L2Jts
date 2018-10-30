package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.GameClient;

/**
 * format: ddd
 */
public class NetPing extends L2GameClientPacket {
    private int clientId, ms, length;

    @Override
    protected void readImpl() {
        clientId = readD();
        ms = readD();
        length = readD();
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();
        if (client == null)
            return;

        client.setLastPing(ms);
    }
}