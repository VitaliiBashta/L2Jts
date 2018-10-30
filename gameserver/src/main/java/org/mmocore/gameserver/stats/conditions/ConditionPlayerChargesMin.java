package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerChargesMin extends Condition {
    private final int _minCharges;

    public ConditionPlayerChargesMin(final int minCharges) {
        _minCharges = minCharges;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!creature.isPlayer()) {
            return false;
        }

        return creature.getIncreasedForce() >= _minCharges;
    }
}
