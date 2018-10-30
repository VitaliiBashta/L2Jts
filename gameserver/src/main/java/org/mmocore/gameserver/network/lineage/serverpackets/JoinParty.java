package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class JoinParty extends GameServerPacket {
    public static final GameServerPacket FAIL = new JoinParty(0, false);

    private final int response;
    private final boolean hasSummon;

    public JoinParty(final int response, final boolean hasSummon) {
        this.response = response;
        this.hasSummon = hasSummon;
    }

    @Override
    protected final void writeData() {
        writeD(response);
        writeD(hasSummon);
    }
}