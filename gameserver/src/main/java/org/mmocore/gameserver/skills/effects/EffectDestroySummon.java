package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectDestroySummon extends Effect {
    public EffectDestroySummon(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (!_effected.isSummon()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Servitor) _effected).unSummon(false, false);
    }

    @Override
    public boolean onActionTime() {
        // just stop this effect
        return false;
    }
}