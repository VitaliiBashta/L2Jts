package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.PositionUtils;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetDirection extends Condition {
    private final PositionUtils.TargetDirection _dir;

    public ConditionTargetDirection(final PositionUtils.TargetDirection direction) {
        _dir = direction;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();
        return PositionUtils.getDirectionTo(target, creature) == _dir;
    }
}
