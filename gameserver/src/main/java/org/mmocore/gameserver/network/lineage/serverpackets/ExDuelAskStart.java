package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExDuelAskStart extends GameServerPacket {
    final String requestor;
    final int isPartyDuel;

    public ExDuelAskStart(final String requestor, final int isPartyDuel) {
        this.requestor = requestor;
        this.isPartyDuel = isPartyDuel;
    }

    @Override
    protected final void writeData() {
        writeS(requestor);
        writeD(isPartyDuel);
    }
}