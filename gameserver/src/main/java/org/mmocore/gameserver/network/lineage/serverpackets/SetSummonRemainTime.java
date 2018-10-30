package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;

public class SetSummonRemainTime extends GameServerPacket {
    private final int maxFed;
    private final int curFed;

    public SetSummonRemainTime(final Servitor summon) {
        curFed = summon.getCurrentFed();
        maxFed = summon.getMaxFed();
    }

    @Override
    protected final void writeData() {
        writeD(maxFed);
        writeD(curFed);
    }
}