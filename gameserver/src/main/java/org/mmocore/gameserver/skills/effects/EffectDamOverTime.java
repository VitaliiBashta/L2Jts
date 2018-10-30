package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

import java.util.Objects;

public class EffectDamOverTime extends Effect {
    // TODO уточнить уровни 1, 2, 9, 10, 11, 12
    private static final int[] bleed = {12, 17, 25, 34, 44, 54, 62, 67, 72, 77, 82, 87};
    private static final int[] poison = {11, 16, 24, 32, 41, 50, 58, 63, 68, 72, 77, 82};

    private final boolean _percent;

    public EffectDamOverTime(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _percent = getTemplate().getParam().getBool("percent", false);
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isDead())
            return false;

        double damage = calc();
        if (_percent)
            damage = _effected.getMaxHp() * _template._value * 0.01;
        if (damage < 2 && getStackOrder() != -1) {
            if (getEffectType() == EffectType.Poison || Objects.equals(getStackType(), "Poison")) {
                damage = poison[getStackOrder() - 1] * getPeriod() / 1000;
            } else if (getEffectType() == EffectType.Bleed || Objects.equals(getStackType(), "Bleed")) {
                damage = bleed[getStackOrder() - 1] * getPeriod() / 1000;
            }
        }

        damage = _effector.calcStat(getSkill().getTemplate().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill());

        if (damage > _effected.getCurrentHp() - 1 && !_effected.isNpc()) {
            if (!getSkill().getTemplate().isOffensive())
                _effected.sendPacket(SystemMsg.NOT_ENOUGH_HP);
            return false;
        }

        if (getSkill().getTemplate().getAbsorbPart() > 0)
            _effector.setCurrentHp(getSkill().getTemplate().getAbsorbPart() * Math.min(_effected.getCurrentHp(), damage) + _effector.getCurrentHp(), false);

        _effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector,
                _effector.isNpc() || _effected == _effector && reflector == null, false, false, true, false);

        return true;
    }
}
