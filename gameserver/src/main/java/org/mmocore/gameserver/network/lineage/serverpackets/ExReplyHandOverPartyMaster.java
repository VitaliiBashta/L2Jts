package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 * @date 20:34/01.09.2011
 */
public class ExReplyHandOverPartyMaster extends GameServerPacket {
    public static final GameServerPacket TRUE = new ExReplyHandOverPartyMaster(true);
    public static final GameServerPacket FALSE = new ExReplyHandOverPartyMaster(false);

    private final boolean isLeader;

    public ExReplyHandOverPartyMaster(final boolean leader) {
        this.isLeader = leader;
    }

    @Override
    protected void writeData() {
        writeD(isLeader);
    }
}
