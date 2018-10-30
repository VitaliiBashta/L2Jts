package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author : Mangol
 */
public class Effect_p_recharge_vital_point_noncount extends Effect {
    public Effect_p_recharge_vital_point_noncount(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
