package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectSleep extends Effect {
    public EffectSleep(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startSleeping();
        _effected.abortAttack(true, true);
        _effected.abortCast(true, true);
        _effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopSleeping();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}