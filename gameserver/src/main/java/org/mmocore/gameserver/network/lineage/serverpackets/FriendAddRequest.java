package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * format: cS
 */
public class FriendAddRequest extends GameServerPacket {
    private final String requestorName;

    public FriendAddRequest(final String requestorName) {
        this.requestorName = requestorName;
    }

    @Override
    protected final void writeData() {
        writeS(requestorName);
    }
}