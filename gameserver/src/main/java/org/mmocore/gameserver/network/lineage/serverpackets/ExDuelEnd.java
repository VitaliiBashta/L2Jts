package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExDuelEnd extends GameServerPacket {
    private final int duelType;

    public ExDuelEnd(final DuelEvent e) {
        duelType = e.getDuelType();
    }

    @Override
    protected final void writeData() {
        writeD(duelType);
    }
}