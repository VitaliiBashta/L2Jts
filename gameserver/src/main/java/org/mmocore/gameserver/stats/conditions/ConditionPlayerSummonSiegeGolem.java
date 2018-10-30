package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 18:37/22.05.2011
 */
public class ConditionPlayerSummonSiegeGolem extends Condition {
    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        Zone zone = player.getZone(ZoneType.RESIDENCE);
        if (zone != null) {
            return false;
        }
        zone = player.getZone(ZoneType.SIEGE);
        if (zone == null) {
            return false;
        }
        final SiegeEvent event = player.getEvent(SiegeEvent.class);
        if (event == null) {
            return false;
        }
        if (event instanceof CastleSiegeEvent) {
            if (zone.getParams().getInteger("residence") != event.getId()) {
                return false;
            }
            if (event.getId() == 5 && player.getZ() > -200) {
                return false;
            }
            if (event.getSiegeClan(CastleSiegeEvent.ATTACKERS, player.getClan()) == null) {
                return false;
            }
        } else {
            final boolean asClan = event.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan()) != null;
            final boolean asPlayer = event.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(player.getObjectId());
            if (!asClan && !asPlayer) {
                return false;
            }
        }

        return true;
    }
}
