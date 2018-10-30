package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExDuelStart extends GameServerPacket {
    private final int duelType;

    public ExDuelStart(final DuelEvent e) {
        duelType = e.getDuelType();
    }

    @Override
    protected final void writeData() {
        writeD(duelType);
    }
}