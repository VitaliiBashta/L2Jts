package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * Format: (ch) dc
 * d: character object id
 * c: 1 if won 0 if failed
 */
public class ExFishingEnd extends GameServerPacket {
    private final int charId;
    private final boolean win;

    public ExFishingEnd(final Player character, final boolean win) {
        charId = character.getObjectId();
        this.win = win;
    }

    @Override
    protected final void writeData() {
        writeD(charId);
        writeC(win ? 1 : 0);
    }
}