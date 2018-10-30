package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class CharacterDeleteFail extends GameServerPacket {
    public static final int REASON_DELETION_FAILED = 0x01;
    public static final int REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER = 0x02;
    public static final int REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED = 0x03;
    final int error;

    public CharacterDeleteFail(final int error) {
        this.error = error;
    }

    @Override
    protected final void writeData() {
        writeD(error);
    }
}