package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.network.lineage.components.NpcString;

/**
 * @author VISTALL
 * @date 16:43/25.03.2011
 */
public abstract class NpcStringContainer extends GameServerPacket {
    protected final NpcString npcString;
    protected final String[] parameters = new String[5];

    protected NpcStringContainer(final NpcString npcString, final String... arg) {
        this.npcString = npcString;
        System.arraycopy(arg, 0, parameters, 0, arg.length);
    }

    protected void writeElements() {
        writeD(npcString.getId());
        for (final String st : parameters) {
            writeS(st);
        }
    }
}
