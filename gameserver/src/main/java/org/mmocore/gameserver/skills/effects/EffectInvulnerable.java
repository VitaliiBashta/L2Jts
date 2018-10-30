package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectInvulnerable extends Effect {
    public EffectInvulnerable(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        return !_effected.isInvul() && super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startHealBlocked();
        _effected.startDamageBlocked();
        _effected.startDebuffImmunity();
        _effected.setIsInvul(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.tryToStopHealBlock(getSkill());
        _effected.tryToStopDamageBlock(getSkill());
        _effected.tryToStopDebuffImmunity(getSkill());
        _effected.setIsInvul(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}