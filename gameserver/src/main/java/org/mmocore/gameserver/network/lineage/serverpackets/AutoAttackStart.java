package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class AutoAttackStart extends GameServerPacket {
    // dh
    private final int targetId;

    public AutoAttackStart(final int targetId) {
        this.targetId = targetId;
    }

    @Override
    protected final void writeData() {
        writeD(targetId);
    }
}