package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public final class ConditionHasSkill extends Condition {
    private final Integer _id;
    private final int _level;

    public ConditionHasSkill(final Integer id, final int level) {
        _id = id;
        _level = level;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!skill.isPresent()) {
            return false;
        }

        return creature.getSkillLevel(_id) >= _level;
    }
}
