package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.Collections;
import java.util.Set;

/**
 * @author VISTALL
 * @date 6:22/12.06.2011
 */
public class ExMpccPartymasterList extends GameServerPacket {
    private Set<String> members = Collections.emptySet();

    public ExMpccPartymasterList(final Set<String> s) {
        members = s;
    }

    @Override
    protected void writeData() {
        writeD(members.size());
        for (final String t : members) {
            writeS(t);
        }
    }
}
