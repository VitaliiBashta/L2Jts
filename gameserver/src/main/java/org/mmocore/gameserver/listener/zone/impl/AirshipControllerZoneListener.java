package org.mmocore.gameserver.listener.zone.impl;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.instances.ClanAirShipControllerInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.Creature;

public class AirshipControllerZoneListener implements OnZoneEnterLeaveListener {
    private ClanAirShipControllerInstance _controllerInstance;

    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        if (_controllerInstance == null && actor instanceof ClanAirShipControllerInstance) {
            _controllerInstance = (ClanAirShipControllerInstance) actor;
        } else if (actor.isClanAirShip()) {
            _controllerInstance.setDockedShip((ClanAirShip) actor);
        }
    }

    @Override
    public void onZoneLeave(final Zone zone, final Creature actor) {
        if (actor.isClanAirShip()) {
            _controllerInstance.setDockedShip(null);
        }
    }
}
