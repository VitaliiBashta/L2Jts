package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectManaHealOverTime extends Effect {
    private final boolean _ignoreMpEff;

    public EffectManaHealOverTime(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isHealBlocked()) {
            return true;
        }

        final double mp = calc();
        final double newMp = mp * (!_ignoreMpEff ? _effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        final double addToMp = Math.max(0, Math.min(newMp, _effected.calcStat(Stats.MP_LIMIT, null, null) * _effected.getMaxMp() / 100. -
                _effected.getCurrentMp()));

        if (addToMp > 0) {
            _effected.setCurrentMp(_effected.getCurrentMp() + addToMp);
        }

        return true;
    }
}