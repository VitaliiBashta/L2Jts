package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectInterrupt extends Effect {
    public EffectInterrupt(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!getEffected().isRaid()) {
            getEffected().abortCast(false, true);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}