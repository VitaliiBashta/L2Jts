package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerOlympiad extends Condition {
    private final boolean _value;

    public ConditionPlayerOlympiad(final boolean v) {
        _value = v;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        return player.isInOlympiadMode() == _value;
    }
}