package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

public class NickNameChanged extends GameServerPacket {
    private final int objectId;
    private final String title;

    public NickNameChanged(final Creature cha) {
        objectId = cha.getObjectId();
        title = cha.getTitle();
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeS(title);
    }
}