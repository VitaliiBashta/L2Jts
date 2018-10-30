package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author nonam3
 * @date 08/01/2011 17:37
 */
public final class EffectRemoveTarget extends Effect {
    private final boolean _doStopTarget;

    public EffectRemoveTarget(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _doStopTarget = template.getParam().getBool("doStopTarget", true);
    }

    @Override
    public void onStart() {
        final Creature target = getEffected();
        if (target.getAI() instanceof DefaultAI) {
            ((DefaultAI) target.getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);
        }

        if (target.getTarget() != null) // каст прерывается только если есть таргет
        {
            target.setTarget(null);
            target.abortCast(true, true);
            if (_doStopTarget) {
                target.stopMove();
            }
        }

        target.abortAttack(true, true);
        target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, getEffector());
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}