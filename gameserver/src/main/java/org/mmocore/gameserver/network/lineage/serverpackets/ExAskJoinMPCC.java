package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Asks the player to join a Command Channel
 */
public class ExAskJoinMPCC extends GameServerPacket {
    private final String requestorName;

    /**
     * @param String Name of CCLeader
     */
    public ExAskJoinMPCC(final String requestorName) {
        this.requestorName = requestorName;
    }

    @Override
    protected void writeData() {
        writeS(requestorName); // лидер CC
        writeD(0x00);
    }
}