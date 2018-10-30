package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectSalvation extends Effect {
    public EffectSalvation(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        return getEffected().isPlayer() && super.checkCondition();
    }

    @Override
    public void onStart() {
        getEffected().setIsSalvation(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        getEffected().setIsSalvation(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}