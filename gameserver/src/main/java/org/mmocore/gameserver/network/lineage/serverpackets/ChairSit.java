package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.StaticObjectInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * format: d
 */
public class ChairSit extends GameServerPacket {
    private final int objectId;
    private final int staticObjectId;

    public ChairSit(final Player player, final StaticObjectInstance throne) {
        objectId = player.getObjectId();
        staticObjectId = throne.getUId();
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(staticObjectId);
    }
}