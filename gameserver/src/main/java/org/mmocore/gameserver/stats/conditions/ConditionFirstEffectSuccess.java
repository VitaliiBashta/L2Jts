package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionFirstEffectSuccess extends Condition {
    final boolean _param;

    public ConditionFirstEffectSuccess(final boolean param) {
        _param = param;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        return initialValue.isPresent() && _param == (initialValue.getAsDouble() == Integer.MAX_VALUE);
    }
}