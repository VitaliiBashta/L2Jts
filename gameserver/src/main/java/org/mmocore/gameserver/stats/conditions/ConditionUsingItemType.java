package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public final class ConditionUsingItemType extends Condition {
    private final long _mask;

    public ConditionUsingItemType(final long mask) {
        _mask = mask;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!creature.isPlayable()) {
            return false;
        }

        return (_mask & ((Playable) creature).getWearedMask()) != 0;
    }
}
