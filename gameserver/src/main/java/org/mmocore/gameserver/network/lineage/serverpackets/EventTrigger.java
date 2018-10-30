package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author SYS
 * @date 10/9/2007
 */
public class EventTrigger extends GameServerPacket {
    private final int trapId;
    private final boolean active;

    public EventTrigger(final int trapId, final boolean active) {
        this.trapId = trapId;
        this.active = active;
    }

    @Override
    protected final void writeData() {
        writeD(trapId); // trap object id
        writeC(active ? 1 : 0); // trap activity 1 or 0
    }
}