package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author KilRoy
 */
public class ExResartResponse extends GameServerPacket {
    @Override
    protected void writeData() {
        writeQ(0x00);
        writeD(0x00);
    }
}