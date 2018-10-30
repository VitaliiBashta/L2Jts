package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerRiding extends Condition {
    private final CheckPlayerRiding _riding;

    public ConditionPlayerRiding(final CheckPlayerRiding riding) {
        _riding = riding;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        if (_riding == CheckPlayerRiding.STRIDER && player.isRiding()) {
            return true;
        }
        if (_riding == CheckPlayerRiding.WYVERN && player.isFlying()) {
            return true;
        }
        if (_riding == CheckPlayerRiding.NONE && !player.isRiding() && !player.isFlying()) {
            return true;
        }

        return false;
    }

    public enum CheckPlayerRiding {
        NONE,
        STRIDER,
        WYVERN
    }
}
