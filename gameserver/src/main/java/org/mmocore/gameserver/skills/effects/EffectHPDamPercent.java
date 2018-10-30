package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectHPDamPercent extends Effect {
    public EffectHPDamPercent(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (_effected.isDead()) {
            return;
        }

        double newHp = (100. - calc()) * _effected.getMaxHp() / 100.;
        newHp = Math.min(_effected.getCurrentHp(), Math.max(0, newHp));
        _effected.setCurrentHp(newHp, false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}