package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 19:15/12.04.2011
 */
public class ConditionPlayerMinMaxDamage extends Condition {
    private final double _min;
    private final double _max;

    public ConditionPlayerMinMaxDamage(final double min, final double max) {
        _min = min;
        _max = max;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!initialValue.isPresent())
            return false;

        final double value = initialValue.getAsDouble();

        if (_min > 0 && value < _min) {
            return false;
        }
        if (_max > 0 && value > _max) {
            return false;
        }
        return true;
    }
}
