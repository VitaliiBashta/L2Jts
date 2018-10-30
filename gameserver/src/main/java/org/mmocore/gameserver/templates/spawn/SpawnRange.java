package org.mmocore.gameserver.templates.spawn;

import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 4:08/19.05.2011
 */
@FunctionalInterface
public interface SpawnRange {
    Location getRandomLoc(int geoIndex);
}
