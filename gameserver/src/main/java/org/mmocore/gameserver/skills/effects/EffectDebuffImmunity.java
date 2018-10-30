package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectDebuffImmunity extends Effect {
    public EffectDebuffImmunity(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        getEffected().startDebuffImmunity();
    }

    @Override
    public void onExit() {
        super.onExit();
        getEffected().tryToStopDebuffImmunity(getSkill());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}