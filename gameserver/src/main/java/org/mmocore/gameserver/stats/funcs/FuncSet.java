package org.mmocore.gameserver.stats.funcs;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public class FuncSet extends Func {
    public FuncSet(final Stats stat, final int order, final Object owner, final double value) {
        super(stat, order, owner, value);
    }

    @Override
    public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
        return value;
    }
}
