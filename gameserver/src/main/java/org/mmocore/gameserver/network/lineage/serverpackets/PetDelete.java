package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PetDelete extends GameServerPacket {
    private final int petId;
    private final int petType;

    public PetDelete(final int petId, final int petType) {
        this.petId = petId;
        this.petType = petType;
    }

    @Override
    protected final void writeData() {
        writeD(petType);
        writeD(petId);
    }
}