package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.serverpackets.ExRegenMax;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectHealOverTime extends Effect {
    private final boolean _ignoreHpEff;

    public EffectHealOverTime(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _ignoreHpEff = template.getParam().getBool("ignoreHpEff", false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getEffected().isPlayer() && getCount() > 0 && getPeriod() > 0) {
            getEffected().sendPacket(new ExRegenMax(calc(), (int) (getCount() * getPeriod() / 1000), (int) (getPeriod() / 1000)));
        }
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isHealBlocked()) {
            return true;
        }

        final double hp = calc();
        final double newHp = hp * (!_ignoreHpEff ? _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        final double addToHp = Math.max(0, Math.min(newHp, _effected.calcStat(Stats.HP_LIMIT, null, null) * _effected.getMaxHp() / 100. -
                _effected.getCurrentHp()));

        if (addToHp > 0) {
            getEffected().setCurrentHp(_effected.getCurrentHp() + addToHp, false);
        }

        return true;
    }
}