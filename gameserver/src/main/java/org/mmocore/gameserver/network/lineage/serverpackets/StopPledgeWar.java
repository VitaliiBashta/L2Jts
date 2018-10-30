package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class StopPledgeWar extends GameServerPacket {
    private final String pledgeName;
    private final String charName;

    public StopPledgeWar(final String pledgeName, final String charName) {
        this.pledgeName = pledgeName;
        this.charName = charName;
    }

    @Override
    protected final void writeData() {
        writeS(pledgeName);
        writeS(charName);
    }
}