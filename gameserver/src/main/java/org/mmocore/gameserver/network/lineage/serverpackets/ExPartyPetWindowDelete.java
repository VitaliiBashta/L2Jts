package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;

public class ExPartyPetWindowDelete extends GameServerPacket {
    private final int summonObjectId;
    private final int ownerObjectId;
    private final String summonName;

    public ExPartyPetWindowDelete(final Servitor summon) {
        summonObjectId = summon.getObjectId();
        summonName = summon.getName();
        ownerObjectId = summon.getPlayer().getObjectId();
    }

    @Override
    protected final void writeData() {
        writeD(summonObjectId);
        writeD(ownerObjectId);
        writeS(summonName);
    }
}