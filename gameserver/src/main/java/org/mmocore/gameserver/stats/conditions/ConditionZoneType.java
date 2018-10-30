package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionZoneType extends Condition {
    private final ZoneType _zoneType;

    public ConditionZoneType(final String zoneType) {
        _zoneType = ZoneType.valueOf(zoneType);
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        return creature.isInZone(_zoneType);
    }
}