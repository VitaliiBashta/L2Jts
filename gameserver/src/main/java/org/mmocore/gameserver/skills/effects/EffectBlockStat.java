package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.skillclasses.NegateStats;

public class EffectBlockStat extends Effect {
    public EffectBlockStat(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.addBlockStats(((NegateStats) _skill.getTemplate()).getNegateStats());
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.removeBlockStats(((NegateStats) _skill.getTemplate()).getNegateStats());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}