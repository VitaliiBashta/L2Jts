package org.mmocore.gameserver.listener.zone.impl;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.player.custom.CustomPlayerComponent;

/**
 * @author Mangol
 * @since 01.05.2016
 */
public class SubscriptionPeaceZoneListener implements OnZoneEnterLeaveListener {
    @Override
    public void onZoneEnter(Zone zone, Creature actor) {
        if (actor == null || !actor.isPlayer()) {
            return;
        }
        final CustomPlayerComponent playerComponent = actor.getPlayer().getCustomPlayerComponent();
        if (!playerComponent.isSubscriptionActive()) {
            return;
        }
        playerComponent.saveSubscriptionTimeTask(false);
        playerComponent.stopSubscriptionTask();
    }

    @Override
    public void onZoneLeave(Zone zone, Creature actor) {
        if (actor == null || !actor.isPlayer()) {
            return;
        }
        final CustomPlayerComponent playerComponent = actor.getPlayer().getCustomPlayerComponent();
        if (!playerComponent.isSubscriptionActive()) {
            return;
        }
        playerComponent.startSubscription();
    }
}
