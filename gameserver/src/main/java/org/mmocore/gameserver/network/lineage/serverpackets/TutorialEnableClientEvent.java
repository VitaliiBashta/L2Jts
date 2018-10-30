package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class TutorialEnableClientEvent extends GameServerPacket {
    private int event = 0;

    public TutorialEnableClientEvent(final int event) {
        this.event = event;
    }

    @Override
    protected final void writeData() {
        writeD(event);
    }
}