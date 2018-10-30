package org.mmocore.gameserver.stats.conditions;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerHasPet extends Condition {
    private final int[] _petIds;

    public ConditionPlayerHasPet(String[] petIds) {
        if (petIds.length == 1 && petIds[0].isEmpty()) {
            _petIds = ArrayUtils.EMPTY_INT_ARRAY;
        } else {
            _petIds = new int[petIds.length];
            for (int i = 0; i < petIds.length; i++) {
                _petIds[i] = Integer.parseInt(petIds[i]);
            }
        }
    }

    @Override
    protected boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Servitor pet = creature.isPet() ? (Servitor) creature : creature.isPlayer() ? ((Player) creature).getServitor() : null;
        if (pet == null || !pet.isPet()) {
            return false;
        }

        if (_petIds.length == 0) {
            return true;
        }
        return ArrayUtils.contains(_petIds, pet.getNpcId());
    }
}