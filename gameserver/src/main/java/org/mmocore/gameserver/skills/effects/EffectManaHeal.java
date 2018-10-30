package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class EffectManaHeal extends Effect {
    private final boolean _ignoreMpEff;

    public EffectManaHeal(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
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

        final double mp = calc();
        final double newMp = Math.min(mp * 1.7, mp * (!_ignoreMpEff ? _effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) /
                100.);
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