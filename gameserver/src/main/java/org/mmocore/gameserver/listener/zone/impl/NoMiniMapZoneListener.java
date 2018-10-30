package org.mmocore.gameserver.listener.zone.impl;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.serverpackets.ShowMiniMap;
import org.mmocore.gameserver.object.Creature;

/**
 * @author KilRoy
 */
public class NoMiniMapZoneListener implements OnZoneEnterLeaveListener {
    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        if (actor.isPlayer() && actor.getPlayer().isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP) && actor.getPlayer().isMiniMapOpened()) {
            actor.sendPacket(new ShowMiniMap(actor.getPlayer(), 0));
            actor.getPlayer().setMiniMapOpened(true, false);
        }
    }

    @Override
    public void onZoneLeave(final Zone zone, final Creature actor) {
    }
}
