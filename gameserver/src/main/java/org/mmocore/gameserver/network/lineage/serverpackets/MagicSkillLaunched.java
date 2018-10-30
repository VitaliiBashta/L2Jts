package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class MagicSkillLaunched extends GameServerPacket {
    private final int casterId;
    private final int skillId;
    private final int skillLevel;
    private final int[] targets;

    public MagicSkillLaunched(final int casterId, final int skillId, final int skillLevel, final int target) {
        this.casterId = casterId;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        targets = new int[]{target};
    }

    public MagicSkillLaunched(final int casterId, final int skillId, final int skillLevel, final int[] targets) {
        this.casterId = casterId;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.targets = targets;
    }

    @Override
    protected final void writeData() {
        writeD(casterId);
        writeD(skillId);
        writeD(skillLevel);
        writeD(targets.length);
        for (final int i : targets) {
            writeD(i);
        }
    }

    @Override
    public L2GameServerPacket packet(final Player player) {
        if (player != null && player.isNotShowBuffAnim()) {
            return null;
        }

        return super.packet(player);
    }
}
