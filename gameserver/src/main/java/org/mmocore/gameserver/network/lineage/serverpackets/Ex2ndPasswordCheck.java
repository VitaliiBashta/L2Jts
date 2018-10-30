package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Format (ch)dd
 * d: window type
 * d: ban user (1)
 */
public class Ex2ndPasswordCheck extends GameServerPacket {
    public static final GameServerPacket PASSWORD_NEW = new Ex2ndPasswordCheck(0x00);
    public static final GameServerPacket PASSWORD_PROMPT = new Ex2ndPasswordCheck(0x01);
    public static final GameServerPacket PASSWORD_OK = new Ex2ndPasswordCheck(0x02);

    private final int windowType;

    public Ex2ndPasswordCheck(final int windowType) {
        this.windowType = windowType;
    }

    @Override
    protected void writeData() {
        writeD(windowType);
        writeD(0x00);
    }
}
