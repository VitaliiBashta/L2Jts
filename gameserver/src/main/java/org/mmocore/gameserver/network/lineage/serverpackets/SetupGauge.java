package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;

public class SetupGauge extends GameServerPacket {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int CYAN = 2;
    public static final int GREEN = 3;

    private final int charId;
    private final int dat1;
    private final int timeStart;
    private final int timeEnd;

    public SetupGauge(final Creature character, final int dat1, final int timeStart) {
        charId = character.getObjectId();
        this.dat1 = dat1;// color  0-blue   1-red  2-cyan  3-
        this.timeStart = timeStart;
        timeEnd = timeStart;
    }

    public SetupGauge(final Creature character, final int dat1, final int timeStart, int timeEnd) {
        charId = character.getObjectId();
        this.dat1 = dat1;// color  0-blue   1-red  2-cyan  3-
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    @Override
    protected final void writeData() {
        writeD(charId);
        writeD(dat1);
        writeD(timeStart);
        writeD(timeEnd); //c2
    }
}