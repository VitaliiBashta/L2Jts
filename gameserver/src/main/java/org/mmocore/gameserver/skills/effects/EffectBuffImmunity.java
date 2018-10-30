package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectBuffImmunity extends Effect {
    public EffectBuffImmunity(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startBuffImmunity();
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.tryToStopBuffImmunity(getSkill());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}
