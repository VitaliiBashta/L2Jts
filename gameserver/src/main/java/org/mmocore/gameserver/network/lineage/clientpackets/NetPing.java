package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.GameClient;

/**
 * format: ddd
 */
public class NetPing extends L2GameClientPacket {
    private int ms;

    @Override
    protected void readImpl() {
        int clientId = readD();
        ms = readD();
        int length = readD();
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();
        if (client == null)
            return;

        client.setLastPing(ms);
    }
}