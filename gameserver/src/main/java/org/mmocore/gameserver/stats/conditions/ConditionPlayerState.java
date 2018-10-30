package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionPlayerState extends Condition {
    private final CheckPlayerState _check;
    private final boolean _required;

    public ConditionPlayerState(final CheckPlayerState check, final boolean required) {
        _check = check;
        _required = required;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        switch (_check) {
            case RESTING: {
                final Player player = creature.getPlayer();

                if (player == null) {
                    return !_required;
                }

                return player.isSitting() == _required;
            }
            case MOVING: {
                return creature.isMoving == _required;
            }
            case RUNNING: {
                return (creature.isMoving && creature.isRunning()) == _required;
            }
            case STANDING: {
                final Player player = creature.getPlayer();

                if (player == null) {
                    return creature.isMoving != _required;
                }

                return player.isSitting() != _required && player.isMoving != _required;
            }
            case FLYING: {
                final Player player = creature.getPlayer();

                if (player == null) {
                    return !_required;
                }

                return creature.isFlying() == _required;
            }
            case FLYING_TRANSFORM: {
                final Player player = creature.getPlayer();

                if (player == null) {
                    return !_required;
                }

                return creature.getPlayer().isInFlyingTransform() == _required;
            }
        }

        return !_required;
    }

    public enum CheckPlayerState {
        RESTING,
        MOVING,
        RUNNING,
        STANDING,
        FLYING,
        FLYING_TRANSFORM
    }
}
