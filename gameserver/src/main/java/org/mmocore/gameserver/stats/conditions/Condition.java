package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public abstract class Condition {
    public static final Condition[] EMPTY_ARRAY = new Condition[0];

    private SystemMsg _message;

    public final SystemMsg getSystemMsg() {
        return _message;
    }

    public final void setSystemMsg(final int msgId) {
        _message = SystemMsg.valueOf(msgId);
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param creature       the effector
     * @param optionalTarget the effected
     * @param skill          the skill
     * @param item           the item
     * @param value          the value
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    protected abstract boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                                        final Optional<ItemInstance> item, final OptionalDouble initialValue);

    public final boolean test(final Creature creature, final Creature target) {
        return testImpl(creature, Optional.ofNullable(target), Optional.empty(), Optional.empty(), OptionalDouble.empty());
    }

    public final boolean test(final Creature creature, final Creature target, final SkillEntry skill) {
        return testImpl(creature, Optional.ofNullable(target), Optional.ofNullable(skill), Optional.empty(), OptionalDouble.empty());
    }

    public final boolean test(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
        return testImpl(creature, Optional.ofNullable(target), Optional.ofNullable(skill), Optional.empty(), OptionalDouble.of(initialValue));
    }

    public final boolean test(final Creature creature, final ItemInstance item) {
        return testImpl(creature, Optional.empty(), Optional.empty(), Optional.ofNullable(item), OptionalDouble.empty());
    }
}
