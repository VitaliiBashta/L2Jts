package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class Ex2ndPasswordVerify extends GameServerPacket {
    public static final int PASSWORD_OK = 0x00;
    public static final int PASSWORD_WRONG = 0x01;
    public static final int PASSWORD_BAN = 0x02;

    private final int wrongTentatives;
    private final int mode;

    public Ex2ndPasswordVerify(final int mode, final int wrongTentatives) {
        this.mode = mode;
        this.wrongTentatives = wrongTentatives;
    }

    @Override
    protected void writeData() {
        writeD(mode);
        writeD(wrongTentatives);
    }
}
