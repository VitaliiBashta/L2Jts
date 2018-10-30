package org.mmocore.gameserver.listener.zone.impl;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.entity.events.objects.TerritoryWardObject;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;

/**
 * @author VISTALL
 * @date 12:48/18.09.2011
 */
public class DominionWardEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new DominionWardEnterLeaveListenerImpl();

    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        if (!actor.isPlayer()) {
            return;
        }

        final Player player = actor.getPlayer();
        final FlagItemAttachment flag = player.getActiveWeaponFlagAttachment();
        if (flag instanceof TerritoryWardObject) {
            flag.onLogout(player);

            player.sendDisarmMessage(((TerritoryWardObject) flag).getWardItemInstance());
        }
    }

    @Override
    public void onZoneLeave(final Zone zone, final Creature actor) {
        //
    }
}
