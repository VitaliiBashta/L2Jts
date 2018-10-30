package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.GameClient;

public class CharacterSelected extends L2GameClientPacket {
    private int _index;

    @Override
    protected void readImpl() {
        _index = readD();
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();
        client.playerSelected(_index);
    }
}