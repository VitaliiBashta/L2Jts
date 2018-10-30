package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class AskJoinPledge extends GameServerPacket {
    private final int requestorId;
    private final String pledgeName;

    public AskJoinPledge(final int requestorId, final String pledgeName) {
        this.requestorId = requestorId;
        this.pledgeName = pledgeName;
    }

    @Override
    protected final void writeData() {
        writeD(requestorId);
        writeS(pledgeName);
    }
}