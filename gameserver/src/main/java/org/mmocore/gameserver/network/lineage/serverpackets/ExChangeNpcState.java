package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExChangeNpcState extends GameServerPacket {
    private final int objId;
    private final int state;

    public ExChangeNpcState(final int objId, final int state) {
        this.objId = objId;
        this.state = state;
    }

    @Override
    protected void writeData() {
        writeD(objId);
        writeD(state);
    }
}
