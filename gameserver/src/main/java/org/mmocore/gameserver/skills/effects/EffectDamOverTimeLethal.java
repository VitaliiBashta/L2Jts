package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectDamOverTimeLethal extends Effect {
    public EffectDamOverTimeLethal(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isDead()) {
            return false;
        }

        double damage = calc();

        if (getSkill().getTemplate().isOffensive()) {
            damage *= 2;
        }

        damage = _effector.calcStat(getSkill().getTemplate().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill());

        _effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && !_effected.equals(_effector), !_effected.equals(_effector),
                _effector.isNpc() || _effected.equals(_effector), false, false, true, false);

        return true;
    }
}