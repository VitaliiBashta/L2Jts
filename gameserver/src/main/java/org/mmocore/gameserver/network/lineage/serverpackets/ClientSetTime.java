package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ClientSetTime extends GameServerPacket {
    public static final GameServerPacket STATIC = new ClientSetTime();

    @Override
    protected final void writeData() {
        writeD(GameTimeManager.getInstance().getGameTime()); // time in client minutes
        writeD(6); //constant to match the server time( this determines the speed of the client clock)
    }
}