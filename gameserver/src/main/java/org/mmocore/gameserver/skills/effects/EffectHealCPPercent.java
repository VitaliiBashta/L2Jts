package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectHealCPPercent extends Effect {
    private final boolean _ignoreCpEff;

    public EffectHealCPPercent(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _ignoreCpEff = template.getParam().getBool("ignoreCpEff", true);
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

        final double cp = calc() * _effected.getMaxCp() / 100.;
        final double newCp = cp * (!_ignoreCpEff ? _effected.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        final double addToCp = Math.max(0, Math.min(newCp, _effected.calcStat(Stats.CP_LIMIT, null, null) * _effected.getMaxCp() / 100. - _effected.getCurrentCp()));

        _effected.sendPacket(new SystemMessage(SystemMsg.S2_CP_HAS_BEEN_RESTORED_BY_C1).addName(_effector).addNumber((long) addToCp));

        if (addToCp > 0) {
            _effected.setCurrentCp(addToCp + _effected.getCurrentCp());
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}