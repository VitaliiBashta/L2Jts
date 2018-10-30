package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExVitalityPointInfo extends GameServerPacket {
    private final int vitality;

    public ExVitalityPointInfo(final int vitality) {
        this.vitality = vitality;
    }

    @Override
    protected void writeData() {
        writeD(vitality);
    }
}