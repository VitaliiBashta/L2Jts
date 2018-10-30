package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class StartPledgeWar extends GameServerPacket {
    private final String pledgeName;
    private final String charName;

    public StartPledgeWar(final String pledge, final String charName) {
        pledgeName = pledge;
        this.charName = charName;
    }

    @Override
    protected final void writeData() {
        writeS(charName);
        writeS(pledgeName);
    }
}