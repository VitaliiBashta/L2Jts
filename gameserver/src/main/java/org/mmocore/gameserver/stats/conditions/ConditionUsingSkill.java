package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 20:57/12.04.2011
 */
public class ConditionUsingSkill extends Condition {
    private final int _id;

    public ConditionUsingSkill(final int id) {
        _id = id;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!skill.isPresent()) {
            return false;
        }

        final SkillEntry skillEntry = skill.get();
        return skillEntry.getId() == _id;
    }
}
