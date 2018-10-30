package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public final class ConditionTargetHasBuff extends Condition {
    private final EffectType _effectType;
    private final int _level;

    public ConditionTargetHasBuff(final EffectType effectType, final int level) {
        _effectType = effectType;
        _level = level;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();
        final Effect effect = target.getEffectList().getEffectByType(_effectType);

        if (effect == null) {
            return false;
        }

        if (_level == -1 || effect.getSkill().getLevel() >= _level) {
            return true;
        }

        return false;
    }
}
