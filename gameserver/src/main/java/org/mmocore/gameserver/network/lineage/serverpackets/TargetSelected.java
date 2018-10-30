package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

/**
 * format   dddddd
 */
public class TargetSelected extends GameServerPacket {
    private final int objectId;
    private final int targetId;
    private final Location loc;

    public TargetSelected(final int objectId, final int targetId, final Location loc) {
        this.objectId = objectId;
        this.targetId = targetId;
        this.loc = loc;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(targetId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(0x00);
    }
}