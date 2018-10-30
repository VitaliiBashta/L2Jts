package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectGrow extends Effect {
    public EffectGrow(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isNpc()) {
            final NpcInstance npc = (NpcInstance) _effected;
            npc.setCollisionHeight(npc.getCollisionHeight() * 1.24);
            npc.setCollisionRadius(npc.getCollisionRadius() * 1.19);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected.isNpc()) {
            final NpcInstance npc = (NpcInstance) _effected;
            npc.setCollisionHeight(npc.getTemplate().getCollisionHeight());
            npc.setCollisionRadius(npc.getTemplate().getCollisionRadius());
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}