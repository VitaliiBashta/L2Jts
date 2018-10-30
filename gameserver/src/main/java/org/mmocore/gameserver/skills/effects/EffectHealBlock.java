package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectHealBlock extends Effect {
    public EffectHealBlock(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isHealBlocked()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startHealBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.tryToStopHealBlock(getSkill());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}