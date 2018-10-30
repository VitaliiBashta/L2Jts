package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectUnAggro extends Effect {
    public EffectUnAggro(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isNpc()) {
            ((NpcInstance) _effected).setUnAggred(true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected.isNpc()) {
            ((NpcInstance) _effected).setUnAggred(false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}