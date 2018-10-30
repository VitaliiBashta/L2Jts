package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 */
public class ConditionPlayerInstanceZone extends Condition {
    private final int _id;

    public ConditionPlayerInstanceZone(final int id) {
        _id = id;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Reflection ref = creature.getReflection();
        return ref.getInstancedZoneId() == _id;
    }
}