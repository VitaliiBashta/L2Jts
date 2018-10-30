package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectMPDamPercent extends Effect {
    public EffectMPDamPercent(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (_effected.isDead()) {
            return;
        }

        double newMp = (100. - calc()) * _effected.getMaxMp() / 100.;
        newMp = Math.min(_effected.getCurrentMp(), Math.max(0, newMp));
        _effected.setCurrentMp(newMp);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}