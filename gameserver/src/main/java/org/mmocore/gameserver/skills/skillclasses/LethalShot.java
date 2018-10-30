package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Formulas.AttackInfo;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class LethalShot extends Skill {
    public LethalShot(final StatsSet set) {
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

                reflected = target.checkReflectSkill(activeChar, skillEntry);
                realTarget = reflected ? activeChar : target;

                if (getPower() > 0) // Если == 0 значит скилл "отключен"
                {
                    final AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, skillEntry, false, false, ss, false);

                    if (info.lethal_dmg > 0) {
                        realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, skillEntry, true, true, false, false, false, false, false, true);
                    }

                    realTarget.reduceCurrentHp(info.damage, activeChar, skillEntry, true, true, false, true, false, false, true);
                    if (!reflected) {
                        realTarget.doCounterAttack(skillEntry, activeChar, false);
                    }
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false, reflected);
            }
        }
    }
}
