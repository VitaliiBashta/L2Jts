package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Fence;

public class ExColosseumFenceInfo extends GameServerPacket {
    private final int type;
    private final Fence instance;
    private final int width;
    private final int height;

    public ExColosseumFenceInfo(final Fence object) {
        instance = object;
        type = instance.getType();
        width = instance.getWidth();
        height = instance.getHeight();
    }

    @Override
    protected void writeData() {
        writeD(instance.getObjectId());
        writeD(type);
        writeD(instance.getX());
        writeD(instance.getY());
        writeD(instance.getZ());
        writeD(width);
        writeD(height);

    }
}
