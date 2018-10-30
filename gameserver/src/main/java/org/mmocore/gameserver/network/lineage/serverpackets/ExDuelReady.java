package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExDuelReady extends GameServerPacket {
    private final int duelType;

    public ExDuelReady(final DuelEvent event) {
        duelType = event.getDuelType();
    }

    @Override
    protected final void writeData() {
        writeD(duelType);
    }
}