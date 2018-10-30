package org.mmocore.gameserver.utils;

import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.world.World;

public class MapUtils {
    private MapUtils() {
    }

    public static int regionX(final GameObject o) {
        return regionX(o.getX());
    }

    public static int regionY(final GameObject o) {
        return regionY(o.getY());
    }

    public static int regionX(final int x) {
        return (x - World.MAP_MIN_X >> 15) + GeodataConfig.GEO_X_FIRST;
    }

    public static int regionY(final int y) {
        return (y - World.MAP_MIN_Y >> 15) + GeodataConfig.GEO_Y_FIRST;
    }
}
