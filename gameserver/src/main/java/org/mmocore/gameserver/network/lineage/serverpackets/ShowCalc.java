package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * sample: d
 */
public class ShowCalc extends GameServerPacket {
    private final int calculatorId;

    public ShowCalc(final int calculatorId) {
        this.calculatorId = calculatorId;
    }

    @Override
    protected final void writeData() {
        writeD(calculatorId);
    }
}