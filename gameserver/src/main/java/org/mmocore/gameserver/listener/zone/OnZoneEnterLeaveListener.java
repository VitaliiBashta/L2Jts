package org.mmocore.gameserver.listener.zone;

import org.mmocore.commons.listener.Listener;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;

public interface OnZoneEnterLeaveListener extends Listener<Zone> {
    void onZoneEnter(Zone zone, Creature actor);

    void onZoneLeave(Zone zone, Creature actor);
}
