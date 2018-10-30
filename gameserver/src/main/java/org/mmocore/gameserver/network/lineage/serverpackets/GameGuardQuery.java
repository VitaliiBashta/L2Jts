package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class GameGuardQuery extends GameServerPacket {
    @Override
    protected final void writeData() {
        writeD(0x00); // ? - Меняется при каждом перезаходе.
        writeD(0x00); // ? - Меняется при каждом перезаходе.
        writeD(0x00); // ? - Меняется при каждом перезаходе.
        writeD(0x00); // ? - Меняется при каждом перезаходе.
    }
}