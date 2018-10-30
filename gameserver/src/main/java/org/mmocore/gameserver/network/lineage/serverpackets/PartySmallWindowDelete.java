package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class PartySmallWindowDelete extends GameServerPacket {
    private final int objId;
    private final String name;

    public PartySmallWindowDelete(final Player member) {
        objId = member.getObjectId();
        name = member.getName();
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeS(name);
    }
}