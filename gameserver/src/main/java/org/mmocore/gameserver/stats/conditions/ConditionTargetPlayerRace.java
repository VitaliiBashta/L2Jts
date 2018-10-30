package org.mmocore.gameserver.stats.conditions;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetPlayerRace extends Condition {
    private final PlayerRace _race;

    public ConditionTargetPlayerRace(final String race) {
        _race = PlayerRace.valueOf(race.toLowerCase());
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

        return _race == targetPlayer.getPlayerTemplateComponent().getPlayerRace();
    }
}