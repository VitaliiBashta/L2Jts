package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author KilRoy
 */
public class ExMembershipInfo extends GameServerPacket {
    @Override
    protected void writeData() {
        writeD(0x00);
    }
}