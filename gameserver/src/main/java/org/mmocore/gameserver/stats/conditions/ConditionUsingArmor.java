package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.item.ArmorTemplate.ArmorType;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionUsingArmor extends Condition {
    private final ArmorType _armor;

    public ConditionUsingArmor(final ArmorType armor) {
        _armor = armor;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        return player.isWearingArmor(_armor);
    }
}
