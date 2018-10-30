package org.mmocore.gameserver.stats.conditions;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerRace extends Condition {
    private final PlayerRace _race;

    public ConditionPlayerRace(final String race) {
        _race = PlayerRace.valueOf(race.toLowerCase());
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        return player.getPlayerTemplateComponent().getPlayerRace() == _race;
    }
}