package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Author: VISTALL
 */
public class ExSubPledgeSkillAdd extends GameServerPacket {
    private final int type;
    private final int id;
    private final int level;

    public ExSubPledgeSkillAdd(final int type, final int id, final int level) {
        this.type = type;
        this.id = id;
        this.level = level;
    }

    @Override
    protected void writeData() {
        writeD(type);
        writeD(id);
        writeD(level);
    }
}