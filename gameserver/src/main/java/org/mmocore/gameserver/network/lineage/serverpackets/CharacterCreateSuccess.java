package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class CharacterCreateSuccess extends GameServerPacket {
    public static final GameServerPacket STATIC = new CharacterCreateSuccess();

    @Override
    protected final void writeData() {
        writeD(0x01);
    }
}