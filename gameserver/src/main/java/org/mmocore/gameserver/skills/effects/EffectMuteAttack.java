package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectMuteAttack extends Effect {
    public EffectMuteAttack(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!_effected.startAMuted()) {
            _effected.abortCast(true, true);
            _effected.abortAttack(true, true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopAMuted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}