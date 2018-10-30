package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ExBR_PremiumState extends GameServerPacket {
    private final int objectId;
    private final int state;

    public ExBR_PremiumState(final Player activeChar, final boolean state) {
        objectId = activeChar.getObjectId();
        this.state = state ? 1 : 0;
    }

    @Override
    protected void writeData() {
        writeD(objectId);
        writeC(state);
    }
}