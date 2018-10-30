package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExAskModifyPartyLooting extends GameServerPacket {
    private final String requestor;
    private final int mode;

    public ExAskModifyPartyLooting(final String name, final int mode) {
        requestor = name;
        this.mode = mode;
    }

    @Override
    protected void writeData() {
        writeS(requestor);
        writeD(mode);
    }
}
