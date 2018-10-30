package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Formulas.AttackInfo;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Drain extends Skill {
    private final double _absorbAbs;

    public Drain(final StatsSet set) {
        super(set);
        _absorbAbs = set.getDouble("absorbAbs", 0.f);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
        final boolean ss = isSSPossible() && activeChar.getChargedSoulShot();
        Creature realTarget;
        boolean reflected;
        final boolean corpseSkill = _targetType == SkillTargetType.TARGET_CORPSE;

        for (final Creature target : targets) {
            if (target != null) {
                reflected = !corpseSkill && target.checkReflectSkill(activeChar, skillEntry);
                realTarget = reflected ? activeChar : target;

                if (getPower() > 0 || _absorbAbs > 0) // Если == 0 значит скилл "отключен"
                {
                    if (realTarget.isDead() && !corpseSkill) {
                        continue;
                    }

                    double hp = 0.;
                    final double targetHp = realTarget.getCurrentHp();

                    if (!corpseSkill) {
                        final AttackInfo info;
                        if (isMagic()) {
                            info = Formulas.calcMagicDam(activeChar, realTarget, skillEntry, sps);
                        } else {
                            info = Formulas.calcPhysDam(activeChar, realTarget, skillEntry, false, false, ss, false);
                        }

                        if (info.lethal_dmg > 0)
                            realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, skillEntry, true, true, false, false, false, false, false, true);

                        final double targetCP = realTarget.getCurrentCp();

                        // Нельзя восстанавливать HP из CP
                        if (info.damage > targetCP || !realTarget.isPlayer()) {
                            hp = (info.damage - targetCP) * _absorbPart;
                        }

                        realTarget.reduceCurrentHp(info.damage, activeChar, skillEntry, true, true, false, true, false, false, true);
                        if (!reflected) {
                            realTarget.doCounterAttack(skillEntry, activeChar, false);
                        }
                    }

                    if (_absorbAbs == 0 && _absorbPart == 0) {
                        continue;
                    }

                    hp += _absorbAbs;

                    // Нельзя восстановить больше hp, чем есть у цели.
                    if (hp > targetHp && !corpseSkill) {
                        hp = targetHp;
                    }

                    final double addToHp = Math.max(0, Math.min(hp, activeChar.calcStat(Stats.HP_LIMIT, null, null) * activeChar.getMaxHp() / 100. - activeChar.getCurrentHp()));

                    if (addToHp > 0 && !activeChar.isHealBlocked()) {
                        activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);
                    }

                    if (realTarget.isDead() && corpseSkill && realTarget.isNpc()) {
                        activeChar.getAI().setAttackTarget(null);
                        ((NpcInstance) realTarget).endDecayTask();
                    }
                }

                if (corpseSkill)
                    target.deleteMe();
                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false, reflected);
            }
        }

        if (isMagic() ? sps != 0 : ss) {
            activeChar.unChargeShots(isMagic());
        }
    }
}