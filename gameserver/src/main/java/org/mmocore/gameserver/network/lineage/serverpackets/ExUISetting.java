package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ExUISetting extends GameServerPacket {
    private final byte[] data;

    public ExUISetting(final Player player) {
        data = player.getKeyBindings();
    }

    @Override
    protected void writeData() {
        writeD(data.length);
        writeB(data);
    }
}
