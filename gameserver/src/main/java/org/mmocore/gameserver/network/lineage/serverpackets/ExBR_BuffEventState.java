package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExBR_BuffEventState extends GameServerPacket {
    private final int type; // 1 - %, 2 - npcId
    private final int value; // depending on type: for type 1 - % value; for type 2 - 20573-20575
    private final int state; // 0-1
    private final int endTime; // unixTime

    public ExBR_BuffEventState(int type, int value, int state, int endTime) {
        this.type = type;
        this.value = value;
        this.state = state;
        this.endTime = endTime;
    }

    @Override
    protected void writeData() {
        writeD(type);
        writeD(value);
        writeD(state);
        writeD(endTime);
    }
}