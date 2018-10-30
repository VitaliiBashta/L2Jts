package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Create by Mangol on 17.10.2015.
 */
public class Effect_p_dominion_transform extends Effect {
    public Effect_p_dominion_transform(Creature creature, Creature target, SkillEntry skill, EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        getEffected().setDominionTransform(true);
        super.onStart();
    }

    @Override
    public void onExit() {
        getEffected().setDominionTransform(false);
        super.onExit();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
