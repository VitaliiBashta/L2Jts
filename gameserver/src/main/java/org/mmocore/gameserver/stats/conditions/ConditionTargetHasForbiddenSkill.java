package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public final class ConditionTargetHasForbiddenSkill extends Condition {
    private final int _skillId;

    public ConditionTargetHasForbiddenSkill(final int skillId) {
        _skillId = skillId;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();

        if (!target.isPlayable()) {
            return false;
        }

        return !(target.getSkillLevel(_skillId) > 0);
    }
}
