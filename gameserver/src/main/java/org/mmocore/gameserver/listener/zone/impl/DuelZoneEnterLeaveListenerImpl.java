package org.mmocore.gameserver.listener.zone.impl;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 15:07/28.08.2011
 */
public class DuelZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new DuelZoneEnterLeaveListenerImpl();

    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        if (!actor.isPlayable()) {
            return;
        }

        final Player player = actor.getPlayer();

        final DuelEvent duelEvent = player.getEvent(DuelEvent.class);
        if (duelEvent != null) {
            duelEvent.playerLost(player);
        }
    }

    @Override
    public void onZoneLeave(final Zone zone, final Creature actor) {

    }
}
