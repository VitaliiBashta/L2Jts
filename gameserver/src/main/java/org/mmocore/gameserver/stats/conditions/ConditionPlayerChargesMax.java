package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerChargesMax extends Condition {
    private final int _maxCharges;

    public ConditionPlayerChargesMax(final int maxCharges) {
        _maxCharges = maxCharges;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!creature.isPlayer()) {
            return false;
        }

        if (creature.getIncreasedForce() >= _maxCharges) {
            creature.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
            return false;
        }

        return true;
    }
}
