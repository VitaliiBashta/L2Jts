package org.mmocore.gameserver.model;

import org.mmocore.commons.geometry.Point3D;
import org.mmocore.commons.geometry.Shape;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Описание территории мира.
 * Содержит список границ включающих в себя территорию и исключающихся из территории.
 *
 * @author G1ta0
 */
public class Territory implements Shape, SpawnRange {
    protected final Point3D max = new Point3D();
    protected final Point3D min = new Point3D();

    private final List<Shape> include = new ArrayList<>(1);
    private final List<Shape> exclude = new ArrayList<>(1);

    public static Location getRandomLoc(final Territory territory) {
        return getRandomLoc(territory, 0);
    }

    public static Location getRandomLoc(final Territory territory, final int geoIndex) {
        final Location pos = new Location();

        final List<Shape> territories = territory.getTerritories();

        loop:
        for (int i = 0; i < 100; i++) {
            final Shape shape = territories.get(Rnd.get(territories.size()));

            pos.x = Rnd.get(shape.getXmin(), shape.getXmax());
            pos.y = Rnd.get(shape.getYmin(), shape.getYmax());
            pos.z = shape.getZmin() + (shape.getZmax() - shape.getZmin()) / 2;

            if (territory.isInside(pos.x, pos.y)) {
                // Не спаунить в колонны, стены и прочее.
                final int tempz = GeoEngine.getHeight(pos, geoIndex);
                if (shape.getZmin() != shape.getZmax()) {
                    if (tempz < shape.getZmin() || tempz > shape.getZmax()) {
                        continue;
                    }
                } else if (tempz < shape.getZmin() - 200 || tempz > shape.getZmin() + 200) {
                    continue;
                }

                pos.z = tempz;

                final int geoX = pos.x - World.MAP_MIN_X >> 4;
                final int geoY = pos.y - World.MAP_MIN_Y >> 4;

                // Если местность подозрительная - пропускаем
                for (int x = geoX - 1; x <= geoX + 1; x++) {
                    for (int y = geoY - 1; y <= geoY + 1; y++) {
                        if (GeoEngine.NgetNSWE(x, y, tempz, geoIndex) != GeoEngine.NSWE_ALL) {
                            continue loop;
                        }
                    }
                }

                return pos;
            }
        }

        return pos;
    }

    public Territory add(final Shape shape) {
        if (include.isEmpty()) {
            max.x = shape.getXmax();
            max.y = shape.getYmax();
            max.z = shape.getZmax();
            min.x = shape.getXmin();
            min.y = shape.getYmin();
            min.z = shape.getZmin();
        } else {
            max.x = Math.max(max.x, shape.getXmax());
            max.y = Math.max(max.y, shape.getYmax());
            max.z = Math.max(max.z, shape.getZmax());
            min.x = Math.min(min.x, shape.getXmin());
            min.y = Math.min(min.y, shape.getYmin());
            min.z = Math.min(min.z, shape.getZmin());
        }

        include.add(shape);
        return this;
    }

    public Territory addBanned(final Shape shape) {
        exclude.add(shape);
        return this;
    }

    public List<Shape> getTerritories() {
        return include;
    }

    public List<Shape> getBannedTerritories() {
        return exclude;
    }

    @Override
    public boolean isInside(final int x, final int y) {
        for (final Shape shape : include)
            if (shape.isInside(x, y))
                return !isExcluded(x, y);

        return false;
    }

    @Override
    public boolean isInside(final int x, final int y, final int z) {
        if (x < this.min.x || x > this.max.x || y < this.min.y || y > this.max.y || z < this.min.z || z > this.max.z) {
            return false;
        }

        for (final Shape shape : include) {
            if (shape.isInside(x, y, z))
                return !isExcluded(x, y, z);
        }
        return false;
    }

    public boolean isExcluded(final int x, final int y) {
        for (final Shape shape : exclude) {
            if (shape.isInside(x, y))
                return true;
        }
        return false;
    }

    public boolean isExcluded(final int x, final int y, final int z) {
        for (final Shape shape : exclude) {
            if (shape.isInside(x, y, z))
                return true;
        }
        return false;
    }

    @Override
    public int getXmax() {
        return this.max.x;
    }

    @Override
    public int getXmin() {
        return this.min.x;
    }

    @Override
    public int getYmax() {
        return this.max.y;
    }

    @Override
    public int getYmin() {
        return this.min.y;
    }

    @Override
    public int getZmax() {
        return this.max.z;
    }

    @Override
    public int getZmin() {
        return this.min.z;
    }

    @Override
    public Location getRandomLoc(final int geoIndex) {
        return getRandomLoc(this, geoIndex);
    }
}