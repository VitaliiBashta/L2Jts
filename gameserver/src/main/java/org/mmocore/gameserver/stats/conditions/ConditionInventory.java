package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public abstract class ConditionInventory extends Condition {
    protected final int _slot;

    public ConditionInventory(final int slot) {
        _slot = slot;
    }

    @Override
    public abstract boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                                     final Optional<ItemInstance> item, final OptionalDouble initialValue);
}