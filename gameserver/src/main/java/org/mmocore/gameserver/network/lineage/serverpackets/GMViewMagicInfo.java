package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author KilRoy
 */
public class GMViewMagicInfo extends GameServerPacket {
    @Override
    protected final void writeData() {
        writeS("None");
        writeD(0x00);

        writeD(0x00);
        writeD(0x00);
        writeD(0x00);
    }
}
