package org.mmocore.gameserver.model.entity.events;

import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.templates.InstantZone;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public class EventInstance extends Reflection {
    @Override
    public void init(InstantZone instantZone) {
        setName(instantZone.getName());
        setInstancedZone(instantZone);
        if (instantZone.getMapX() >= 0) {
            final int geoIndex = GeoEngine.NextGeoIndex(instantZone.getMapX(), instantZone.getMapY(), getId());
            setGeoIndex(geoIndex);
        }
        init0(instantZone.getDoors(), instantZone.getZones());
        onCreate();
    }
}
