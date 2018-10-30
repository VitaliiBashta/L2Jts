package org.mmocore.gameserver.stats.conditions;


import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;


public final class ConditionTargetHasBuffId extends Condition {
    private final int _id;
    private final int _level;

    public ConditionTargetHasBuffId(final int id, final int level) {
        _id = id;
        _level = level;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();

        if (_level == -1) {
            return target.getEffectList().getEffectsBySkillId(_id) != null;
        }

        final List<Effect> el = target.getEffectList().getEffectsBySkillId(_id);

        if (el == null) {
            return false;
        }

        for (final Effect effect : el) {
            if (effect != null && effect.getSkill().getLevel() >= _level) {
                return true;
            }
        }

        return false;
    }
}
