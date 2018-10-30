package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class StopAllianceWar extends GameServerPacket {
    private final String allianceName;
    private final String charName;

    public StopAllianceWar(final String allianceName, final String charName) {
        this.allianceName = allianceName;
        this.charName = charName;
    }

    @Override
    protected final void writeData() {
        writeS(allianceName);
        writeS(charName);
    }
}