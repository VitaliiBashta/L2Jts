package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectHeal extends Effect {
    private final boolean _ignoreHpEff;

    public EffectHeal(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _ignoreHpEff = template.getParam().getBool("ignoreHpEff", false);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isHealBlocked()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (_effected.isHealBlocked()) {
            return;
        }

        final double hp = calc();
        final double newHp = hp * (!_ignoreHpEff ? _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        final double addToHp = Math.max(0, Math.min(newHp, _effected.calcStat(Stats.HP_LIMIT, null, null) * _effected.getMaxHp() / 100. -
                _effected.getCurrentHp()));

        if (_effected.isPlayer()) {
            if (!_effector.equals(_effected)) {
                _effected.sendPacket(new SystemMessage(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(_effector.getName()).addNumber(Math.round(
                        addToHp)));
            } else {
                _effected.sendPacket(new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber(Math.round(addToHp)));
            }
        }
        if (addToHp > 0) {
            _effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}