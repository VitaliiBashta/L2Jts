package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author KilRoy
 */
public class ExSay2Fail extends GameServerPacket {
    @Override
    protected void writeData() {
        writeS("None");
        writeD(0x00);
    }
}