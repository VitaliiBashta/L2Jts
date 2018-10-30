package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * sample
 * <p/>
 * 7d
 * c1 b2 e0 4a
 * 00 00 00 00
 * <p/>
 * <p/>
 * format
 * cdd
 */
public class AskJoinAlliance extends GameServerPacket {
    private final String requestorName;
    private final String requestorAllyName;
    private final int requestorId;

    public AskJoinAlliance(final int requestorId, final String requestorName, final String requestorAllyName) {
        this.requestorName = requestorName;
        this.requestorAllyName = requestorAllyName;
        this.requestorId = requestorId;
    }

    @Override
    protected final void writeData() {
        writeD(requestorId);
        writeS(requestorName);
        writeS("");
        writeS(requestorAllyName);
    }
}