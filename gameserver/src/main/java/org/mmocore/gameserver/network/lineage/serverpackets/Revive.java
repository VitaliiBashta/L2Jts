package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.GameObject;

/**
 * sample
 * 0000: 0c  9b da 12 40                                     ....@
 * <p/>
 * format  d
 */
public class Revive extends GameServerPacket {
    private final int objectId;

    public Revive(final GameObject obj) {
        objectId = obj.getObjectId();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
    }
}