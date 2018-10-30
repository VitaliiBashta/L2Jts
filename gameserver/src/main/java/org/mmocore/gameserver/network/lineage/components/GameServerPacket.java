package org.mmocore.gameserver.network.lineage.components;

import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;

/**
 * @author: Camelion, KilRoy
 * @date: 01.08.13/0:07
 */
public abstract class GameServerPacket extends L2GameServerPacket {
    protected abstract void writeData();

    @Override
    protected final void writeImpl() {
        final FirstServerPacketOpcode firstOpcode = ServerOpcodeManager.getInstance().getFirstOpcode(getClass());
        final SecondServerPacketOpcode secondOpcode = ServerOpcodeManager.getInstance().getSecondOpcode(getClass());

        writeC(firstOpcode.ordinal());
        if (secondOpcode != null) {
            writeH(secondOpcode.ordinal());
        }

        writeData();
    }
}
