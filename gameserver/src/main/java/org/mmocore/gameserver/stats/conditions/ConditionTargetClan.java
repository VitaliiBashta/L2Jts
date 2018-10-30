package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetClan extends Condition {
    private final boolean _test;

    public ConditionTargetClan(final String param) {
        _test = Boolean.parseBoolean(param);
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null || !optionalTarget.isPresent()) {
            return false;
        }

        final Creature target = optionalTarget.get();
        final Player targetPlayer = target.getPlayer();

        if (targetPlayer == null) {
            return false;
        }

        return (player.getClanId() != 0 && player.getClanId() == targetPlayer.getClanId() == _test
                || player.isInParty() && player.getParty() == targetPlayer.getParty() == _test);
    }
}