package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectBlessNoblesse extends Effect {
    public EffectBlessNoblesse(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        getEffected().setIsBlessedByNoblesse(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        getEffected().setIsBlessedByNoblesse(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}