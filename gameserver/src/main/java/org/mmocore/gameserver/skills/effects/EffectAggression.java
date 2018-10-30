package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.ai.PlayerAI;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectAggression extends Effect {
    public EffectAggression(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isPlayer() && !_effected.equals(_effector)) {
            ((PlayerAI) _effected.getAI()).lockTarget(_effector);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected.isPlayer() && !_effected.equals(_effector)) {
            ((PlayerAI) _effected.getAI()).lockTarget(null);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}