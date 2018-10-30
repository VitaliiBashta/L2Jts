package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Balance extends Skill {
    public Balance(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        double summaryCurrentHp = 0;
        double summaryMaximumHp = 0;

        for (final Creature target : targets) {
            if (target != null) {
                if (target.isAlikeDead()) {
                    continue;
                }
                summaryCurrentHp += target.getCurrentHp();
                summaryMaximumHp += target.getMaxHp();
            }
        }

        final double percent = summaryCurrentHp / summaryMaximumHp;

        for (final Creature target : targets) {
            if (target != null) {
                if (target.isAlikeDead()) {
                    continue;
                }

                final double hp = target.getMaxHp() * percent;
                if (hp > target.getCurrentHp()) {
                    // увеличение HP, не выше лимита
                    final double limit = target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100.;
                    if (target.getCurrentHp() < limit) // не "подрезаем" HP под лимит если больше
                    {
                        target.setCurrentHp(Math.min(hp, limit), false);
                    }
                } else
                // уменьшение HP, не ниже 1.01 для предотвращения "ложной смерти" на олимпе/дуэли
                {
                    target.setCurrentHp(Math.max(1.01, hp), false);
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
