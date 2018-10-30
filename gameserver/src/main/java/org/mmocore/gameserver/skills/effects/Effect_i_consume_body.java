package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class Effect_i_consume_body extends Effect {
    public Effect_i_consume_body(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        if (getEffected().isNpc()) {
            getEffector().getAI().setAttackTarget(null);
            getEffector().setTarget(null);
            ((NpcInstance) getEffected()).endDecayTask();
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
