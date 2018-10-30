package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectEnervation extends Effect {
    public EffectEnervation(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isNpc()) {
            ((NpcInstance) _effected).setParameter("DebuffIntention", 0.5);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected.isNpc()) {
            ((NpcInstance) _effected).setParameter("DebuffIntention", 1.);
        }
    }
}