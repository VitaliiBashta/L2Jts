package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 15:08/05.08.2011
 */
public class ConditionPlayerHasSummon extends Condition {
    private final boolean _value;

    public ConditionPlayerHasSummon(final boolean value) {
        _value = value;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!creature.isPlayer()) {
            return false;
        }

        return creature.getServitor() != null && _value;
    }
}
