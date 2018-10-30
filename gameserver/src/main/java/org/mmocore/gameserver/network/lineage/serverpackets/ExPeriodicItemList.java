package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExPeriodicItemList extends GameServerPacket {
    @Override
    protected void writeData() {
        writeD(0); // count of dd
    }
}