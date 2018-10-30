package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionGameTime extends Condition {
    private final CheckGameTime _check;
    private final boolean _required;

    public ConditionGameTime(final CheckGameTime check, final boolean required) {
        _check = check;
        _required = required;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        switch (_check) {
            case NIGHT:
                return GameTimeManager.getInstance().isNowNight() == _required;
        }
        return !_required;
    }

    public enum CheckGameTime {
        NIGHT
    }
}
