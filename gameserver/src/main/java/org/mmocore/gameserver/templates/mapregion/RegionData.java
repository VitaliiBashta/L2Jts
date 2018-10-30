package org.mmocore.gameserver.templates.mapregion;

import org.mmocore.gameserver.model.Territory;

@FunctionalInterface
public interface RegionData {
    Territory getTerritory();
}
