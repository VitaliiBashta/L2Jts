package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author Mangol
 */
public class Condition_op_check_abnormal extends Condition {
    private final String _stack_type;
    private final int _stack_order;
    private final Target _target;

    public Condition_op_check_abnormal(final String stack_type, final int stack_order, final String target) {
        _stack_type = stack_type;
        _stack_order = stack_order;
        _target = Target.valueOf(target);
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (_target == Target.SELF) {
            return creature.getEffectList().getAllEffects().stream().anyMatch(e -> e.getStackType().equalsIgnoreCase(_stack_type) && e.getStackOrder() >= _stack_order);
        } else if (_target == Target.TARGET) {
            return optionalTarget.isPresent() && optionalTarget.get().getEffectList().getAllEffects().stream().anyMatch(e -> e.getStackType().equalsIgnoreCase(_stack_type) && e.getStackOrder() >= _stack_order);
        }
        return false;
    }

    private enum Target {
        TARGET,
        SELF
    }
}
