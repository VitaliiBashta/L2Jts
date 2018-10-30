package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExResponseShowContents extends GameServerPacket {
    private final String contents;

    public ExResponseShowContents(final String contents) {
        this.contents = contents;
    }

    @Override
    protected void writeData() {
        writeS(contents);
    }
}