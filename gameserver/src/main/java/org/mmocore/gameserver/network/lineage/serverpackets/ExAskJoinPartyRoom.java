package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Format: ch S
 */
public class ExAskJoinPartyRoom extends GameServerPacket {
    private final String charName;
    private final String roomName;

    public ExAskJoinPartyRoom(final String charName, final String roomName) {
        this.charName = charName;
        this.roomName = roomName;
    }

    @Override
    protected final void writeData() {
        writeS(charName);
        writeS(roomName);
    }
}