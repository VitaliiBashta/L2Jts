package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;

public class PetStatusShow extends GameServerPacket {
    private final int summonType;

    public PetStatusShow(final Servitor summon) {
        summonType = summon.getServitorType();
    }

    @Override
    protected final void writeData() {
        writeD(summonType);
    }
}