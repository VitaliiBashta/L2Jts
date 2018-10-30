package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectMute extends Effect {
    public EffectMute(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!_effected.startMuted()) {
            final SkillEntry castingSkill = _effected.getCastingSkill();
            if (castingSkill != null && castingSkill.getTemplate().isMagic()) {
                _effected.abortCast(true, true);
            }
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopMuted();
    }
}