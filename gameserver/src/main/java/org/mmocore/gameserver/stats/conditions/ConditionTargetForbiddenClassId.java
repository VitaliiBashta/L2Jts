package org.mmocore.gameserver.stats.conditions;

import gnu.trove.set.hash.TIntHashSet;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetForbiddenClassId extends Condition {
    private final TIntHashSet _classIds = new TIntHashSet();

    public ConditionTargetForbiddenClassId(final String[] ids) {
        for (final String id : ids) {
            _classIds.add(Integer.parseInt(id));
        }
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();
        final Player targetPlayer = target.getPlayer();

        if (targetPlayer == null) {
            return false;
        }

        return !_classIds.contains(targetPlayer.getPlayerClassComponent().getActiveClassId());
    }
}