package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionLogicOr extends Condition {
    private static final Condition[] emptyConditions = new Condition[0];

    public Condition[] _conditions = emptyConditions;

    public void add(final Condition condition) {
        if (condition == null) {
            return;
        }

        final int len = _conditions.length;
        final Condition[] tmp = new Condition[len + 1];
        System.arraycopy(_conditions, 0, tmp, 0, len);
        tmp[len] = condition;
        _conditions = tmp;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        for (final Condition c : _conditions) {
            if (c.testImpl(creature, optionalTarget, skill, item, initialValue)) {
                return true;
            }
        }

        return false;
    }
}
