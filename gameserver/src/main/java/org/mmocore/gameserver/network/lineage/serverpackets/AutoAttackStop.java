package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class AutoAttackStop extends GameServerPacket {
    // dh
    private final int targetId;

    /**
     * @param _characters
     */
    public AutoAttackStop(final int targetId) {
        this.targetId = targetId;
    }

    @Override
    protected final void writeData() {
        writeD(targetId);
    }
}