package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class Effect_p_recovery_vp extends Effect {
    private final int param;

    public Effect_p_recovery_vp(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        param = getTemplate().getParam().getInteger("argument");
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    public boolean isRecovery() {
        return param > 0;
    }
}
