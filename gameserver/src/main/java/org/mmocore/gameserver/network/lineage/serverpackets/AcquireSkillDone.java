package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Reworked: VISTALL
 */
public class AcquireSkillDone extends GameServerPacket {
    public static final GameServerPacket STATIC = new AcquireSkillDone();

    @Override
    protected void writeData() {
    }
}