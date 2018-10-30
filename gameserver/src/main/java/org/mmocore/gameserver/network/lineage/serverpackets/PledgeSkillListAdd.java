package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeSkillListAdd extends GameServerPacket {
    private final int skillId;
    private final int skillLevel;

    public PledgeSkillListAdd(final int skillId, final int skillLevel) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
    }

    @Override
    protected final void writeData() {
        writeD(skillId);
        writeD(skillLevel);
    }
}