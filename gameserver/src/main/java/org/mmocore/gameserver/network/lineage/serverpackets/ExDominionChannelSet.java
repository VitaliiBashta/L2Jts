package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExDominionChannelSet extends GameServerPacket {
    public static final GameServerPacket ACTIVE = new ExDominionChannelSet(1);
    public static final GameServerPacket DEACTIVE = new ExDominionChannelSet(0);

    private final int active;

    public ExDominionChannelSet(final int active) {
        this.active = active;
    }

    @Override
    protected void writeData() {
        writeD(active);
    }
}