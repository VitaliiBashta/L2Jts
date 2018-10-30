package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectPetrification extends Effect {
    public EffectPetrification(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isParalyzeImmune()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startParalyzed();
        _effected.startDebuffImmunity();
        _effected.startBuffImmunity();
        _effected.startDamageBlocked();
        _effected.abortAttack(true, true);
        _effected.abortCast(true, true);
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.tryToStopParalyzation(getSkill());
        _effected.tryToStopDebuffImmunity(getSkill());
        _effected.tryToStopBuffImmunity(getSkill());
        _effected.tryToStopDamageBlock(getSkill());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}