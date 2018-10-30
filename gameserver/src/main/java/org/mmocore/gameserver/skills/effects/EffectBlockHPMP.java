package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author Mangol
 * @since 26.09.2016
 */
public class EffectBlockHPMP extends Effect {
    public EffectBlockHPMP(Creature creature, Creature target, SkillEntry skill, EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        _effected.startHealBlocked();
        _effected.startDamageBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();

        _effected.tryToStopHealBlock(getSkill());
        _effected.tryToStopDamageBlock(getSkill());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}
