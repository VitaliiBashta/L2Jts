package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectStun extends Effect {
    public EffectStun(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        return Rnd.chance(_template.chance(100));
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startStunning();
        _effected.abortAttack(true, true);
        _effected.abortCast(true, true);
        _effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopStunning();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}