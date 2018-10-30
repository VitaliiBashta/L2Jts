package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerClassId extends Condition {
    private final int[] _classIds;

    public ConditionPlayerClassId(final String[] ids) {
        _classIds = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            _classIds[i] = Integer.parseInt(ids[i]);
        }
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        final int playerClassId = player.getPlayerClassComponent().getActiveClassId();

        for (final int id : _classIds) {
            if (playerClassId == id) {
                return true;
            }
        }

        return false;
    }
}