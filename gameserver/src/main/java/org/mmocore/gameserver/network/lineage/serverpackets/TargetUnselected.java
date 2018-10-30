package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.utils.Location;

/**
 * format  ddddd
 */
public class TargetUnselected extends GameServerPacket {
    private final int targetId;
    private final Location loc;

    public TargetUnselected(final GameObject obj) {
        targetId = obj.getObjectId();
        loc = obj.getLoc();
    }

    @Override
    protected final void writeData() {
        writeD(targetId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(0x00); // иногда бывает 1
    }
}