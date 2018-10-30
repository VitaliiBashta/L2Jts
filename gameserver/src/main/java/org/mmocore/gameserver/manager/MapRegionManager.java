package org.mmocore.gameserver.manager;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.templates.mapregion.RegionData;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

/**
 * Менеджер специальных зон регионов.
 *
 * @author G1ta0
 */
public class MapRegionManager extends AbstractHolder {
    private final RegionData[][][] map = new RegionData[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][0];

    private MapRegionManager() {
    }

    public static MapRegionManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private int regionX(final int x) {
        return (x - World.MAP_MIN_X >> 15);
    }

    private int regionY(final int y) {
        return (y - World.MAP_MIN_Y >> 15);
    }

    public void addRegionData(final RegionData rd) {
        for (int x = regionX(rd.getTerritory().getXmin()); x <= regionX(rd.getTerritory().getXmax()); x++) {
            for (int y = regionY(rd.getTerritory().getYmin()); y <= regionY(rd.getTerritory().getYmax()); y++) {
                map[x][y] = ArrayUtils.add(map[x][y], rd);
            }
        }
    }

    public <T extends RegionData> T getRegionData(final Class<T> clazz, final GameObject o) {
        return getRegionData(clazz, o.getX(), o.getY(), o.getZ());
    }

    public <T extends RegionData> T getRegionData(final Class<T> clazz, final Location loc) {
        return getRegionData(clazz, loc.getX(), loc.getY(), loc.getZ());
    }

    public <T extends RegionData> T getRegionData(final Class<T> clazz, final int x, final int y, final int z) {
        for (final RegionData rd : map[regionX(x)][regionY(y)]) {
            if (!rd.getClass().equals(clazz)) {
                continue;
            }
            if (rd.getTerritory().isInside(x, y, z)) {
                return (T) rd;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return World.WORLD_SIZE_X * World.WORLD_SIZE_Y;
    }

    @Override
    public void clear() {
    }

    private static class LazyHolder {
        private static final MapRegionManager INSTANCE = new MapRegionManager();
    }
}
