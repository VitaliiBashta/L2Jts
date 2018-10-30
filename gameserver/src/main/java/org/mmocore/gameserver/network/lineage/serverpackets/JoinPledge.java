package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class JoinPledge extends GameServerPacket {
    private final int pledgeId;

    public JoinPledge(final int pledgeId) {
        this.pledgeId = pledgeId;
    }

    @Override
    protected final void writeData() {
        writeD(pledgeId);
    }
}