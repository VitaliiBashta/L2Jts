package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Formulas.AttackInfo;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class PDam extends Skill {
    private final boolean _onCrit;
    private final boolean _directHp;
    private final boolean _blow;

    public PDam(final StatsSet set) {
        super(set);
        _onCrit = set.getBool("onCrit", false);
        _directHp = set.getBool("directHp", false);
        _blow = set.getBool("blow", false);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final boolean ss = activeChar.getChargedSoulShot() && isSSPossible();

        Creature realTarget;
        boolean reflected;
        boolean isHit = false;

        for (final Creature target : targets) {
            if (target != null && !target.isDead()) {
                reflected = target.checkReflectSkill(activeChar, skillEntry);
                realTarget = reflected ? activeChar : target;

                final AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, skillEntry, false, _blow, ss, _onCrit);
                isHit |= !info.miss;

                if (info.lethal_dmg > 0) {
                    realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, skillEntry, true, true, false, false, false, false, false, true);
                }

                if (!info.miss || info.damage >= 1) {
                    realTarget.reduceCurrentHp(info.damage, activeChar, skillEntry, true, true, !info.lethal && _directHp, true, false, false, getPower() != 0);
                }

                if (!reflected) {
                    realTarget.doCounterAttack(skillEntry, activeChar, _blow);
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false, reflected);
            }
        }

        if (isSuicideAttack()) {
            activeChar.doDie(null);
        } else if (isHit && isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}