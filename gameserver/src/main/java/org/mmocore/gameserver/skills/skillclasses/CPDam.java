package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class CPDam extends Skill {
    public CPDam(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
        if (ss) {
            activeChar.unChargeShots(false);
        }

        Creature realTarget;
        boolean reflected;

        for (final Creature target : targets) {
            if (target != null) {
                if (target.isDead()) {
                    continue;
                }

                target.doCounterAttack(skillEntry, activeChar, false);

                reflected = target.checkReflectSkill(activeChar, skillEntry);
                realTarget = reflected ? activeChar : target;

                if (realTarget.isCurrentCpZero()) {
                    continue;
                }

                double damage = _power * realTarget.getCurrentCp();

                if (damage < 1) {
                    damage = 1;
                }

                realTarget.reduceCurrentHp(damage, activeChar, skillEntry, true, true, false, true, false, false, true);

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false, reflected);
            }
        }
    }
}