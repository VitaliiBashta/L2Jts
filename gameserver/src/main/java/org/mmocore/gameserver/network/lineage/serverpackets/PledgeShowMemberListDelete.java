package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeShowMemberListDelete extends GameServerPacket {
    private final String player;

    public PledgeShowMemberListDelete(final String playerName) {
        player = playerName;
    }

    @Override
    protected final void writeData() {
        writeS(player);
    }
}