package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 7:55/03.10.2011
 */
public class Condition_can_transform_in_dominion extends Condition {
    private final int _id;

    public Condition_can_transform_in_dominion(final int id) {
        _id = id;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Optional<DominionSiegeEvent> dominionSiegeEvent = Optional.ofNullable(creature.getEvent(DominionSiegeEvent.class));
        if (!dominionSiegeEvent.isPresent() || dominionSiegeEvent.get().getId() != _id) {
            creature.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.get()));
            return false;
        } else if (!dominionSiegeEvent.get().hasState(DominionSiegeEvent.BATTLEFIELD_CHAT_STATE) || dominionSiegeEvent.get().isInProgress()) {
            creature.sendPacket(new SystemMessage(SystemMsg.THE_TERRITORY_WAR_EXCLUSIVE_DISGUISE_AND_TRANSFORMATION_CAN_BE_USED_20_MINUTES_BEFORE_THE_START_OF_THE_TERRITORY_WAR_TO_10_MINUTES_AFTER_ITS_END));
            return false;
        }
        return dominionSiegeEvent.get().getId() == _id;
    }
}
