package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectManaHealPercent extends Effect {
    private final boolean _ignoreMpEff;

    public EffectManaHealPercent(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _ignoreMpEff = template.getParam().getBool("ignoreMpEff", true);
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

        final double mp = calc() * _effected.getMaxMp() / 100.;
        final double newMp = mp * (!_ignoreMpEff ? _effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        final double addToMp = Math.max(0, Math.min(newMp, _effected.calcStat(Stats.MP_LIMIT, null, null) * _effected.getMaxMp() / 100. -
                _effected.getCurrentMp()));

        _effected.sendPacket(new SystemMessage(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addNumber(Math.round(addToMp)));

        if (addToMp > 0) {
            _effected.setCurrentMp(addToMp + _effected.getCurrentMp());
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}