package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * Format:   dddddddddh [h] h [ddd]
 * Пример пакета:
 * 48
 * 86 99 00 4F  86 99 00 4F
 * EF 08 00 00  01 00 00 00
 * 00 00 00 00  00 00 00 00
 * F9 B5 FF FF  7D E0 01 00  68 F3 FF FF
 * 00 00 00 00
 */
public class MagicSkillUse extends GameServerPacket {
    private final int targetId;
    private final int skillId;
    private final int skillLevel;
    private final int hitTime;
    private final int reuseDelay;
    private final int chaId;
    private final int x;
    private final int y;
    private final int z;
    private final int tx;
    private final int ty;
    private final int tz;

    public MagicSkillUse(final Creature cha, final Creature target, final int skillId, final int skillLevel, final int hitTime, final long reuseDelay) {
        chaId = cha.getObjectId();
        targetId = target.getObjectId();
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.hitTime = hitTime;
        this.reuseDelay = (int) reuseDelay;
        x = cha.getX();
        y = cha.getY();
        z = cha.getZ();
        tx = target.getX();
        ty = target.getY();
        tz = target.getZ();
    }

    public MagicSkillUse(final Creature cha, final int skillId, final int skillLevel, final int hitTime, final long reuseDelay) {
        chaId = cha.getObjectId();
        targetId = cha.getTargetId();
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.hitTime = hitTime;
        this.reuseDelay = (int) reuseDelay;
        x = cha.getX();
        y = cha.getY();
        z = cha.getZ();
        tx = cha.getX();
        ty = cha.getY();
        tz = cha.getZ();
    }

    @Override
    protected final void writeData() {
        writeD(chaId);
        writeD(targetId);
        writeD(skillId);
        writeD(skillLevel);
        writeD(hitTime);
        writeD(reuseDelay);
        writeD(x);
        writeD(y);
        writeD(z);
        writeD(0x00); // unknown
        writeD(tx);
        writeD(ty);
        writeD(tz);
    }

    @Override
    public L2GameServerPacket packet(final Player player) {
        if (player != null && player.isNotShowBuffAnim() && player.getObjectId() != chaId) {
            return null;
        }

        return super.packet(player);
    }
}