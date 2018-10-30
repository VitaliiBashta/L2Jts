package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class TeleportNpc extends Skill {
    public TeleportNpc(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null && !target.isDead()) {
                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
                target.abortAttack(true, true);
                target.abortCast(true, true);
                target.stopMove();
                int x = activeChar.getX();
                int y = activeChar.getY();
                final int z = activeChar.getZ();
                final int h = activeChar.getHeading();
                final double range = activeChar.getColRadius() + target.getColRadius();
                final int hyp = (int) Math.sqrt(range * range / 2.0D);
                if (h < 16384) {
                    x += hyp;
                    y += hyp;
                } else if (h > 16384 && h <= 32768) {
                    x -= hyp;
                    y += hyp;
                } else if (h < 32768 && h <= 49152) {
                    x -= hyp;
                    y -= hyp;
                } else if (h > 49152) {
                    x += hyp;
                    y -= hyp;
                }
                target.setXYZ(x, y, z);
                target.validateLocation(1);
            }
        }
    }
}