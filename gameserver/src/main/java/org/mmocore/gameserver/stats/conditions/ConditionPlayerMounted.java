package org.mmocore.gameserver.stats.conditions;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerMounted extends Condition {
    private int[] _mountIds;

    public ConditionPlayerMounted(String[] mountIds) {
        if (mountIds.length == 1 && mountIds[0].isEmpty()) {
            _mountIds = ArrayUtils.EMPTY_INT_ARRAY;
        } else {
            _mountIds = new int[mountIds.length];
            for (int i = 0; i < mountIds.length; i++) {
                _mountIds[i] = Integer.parseInt(mountIds[i]);
            }
        }
    }

    @Override
    protected boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!creature.isPlayer() || !((Player) creature).isMounted()) {
            return false;
        }

        if (_mountIds.length == 0) {
            return true;
        }
        return ArrayUtils.contains(_mountIds, ((Player) creature).getMountNpcId());
    }
}
