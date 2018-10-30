package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectCPDamPercent extends Effect {
    public EffectCPDamPercent(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (_effected.isDead()) {
            return;
        }

        double newCp = (100. - calc()) * _effected.getMaxCp() / 100.;
        newCp = Math.min(_effected.getCurrentCp(), Math.max(0, newCp));
        _effected.setCurrentCp(newCp);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public boolean refreshCpOnAdd() {
        return false;
    }
}