package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExChangeClientEffectInfo extends GameServerPacket {
    private final int state;

    public ExChangeClientEffectInfo(final int state) {
        this.state = state;
    }

    @Override
    protected void writeData() {
        writeD(0);
        writeD(state);
    }
}
