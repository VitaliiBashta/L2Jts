package org.mmocore.gameserver.utils;

import org.jdom2.Element;
import org.mmocore.commons.geometry.Point2D;
import org.mmocore.commons.geometry.Point3D;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.world.World;

import java.io.Serializable;
import java.util.List;

public class Location extends Point3D implements SpawnRange, Serializable {
    public int h;

    public Location() {
    }

    /**
     * Позиция (x, y, z, heading)
     */
    public Location(final int x, final int y, final int z, final int heading) {
        super(x, y, z);
        h = heading;
    }

    public Location(final int x, final int y, final int z) {
        this(x, y, z, 0);
    }

    public Location(final GameObject obj) {
        this(obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
    }

    /**
     * Парсит Location из строки, где координаты разделены пробелами или запятыми
     */
    public static Location parseLoc(final String s) throws IllegalArgumentException {
        final String[] xyzh = s.split("[\\s,;]+");
        if (xyzh.length < 3) {
            throw new IllegalArgumentException("Can't parse location from string: " + s);
        }
        final int x = Integer.parseInt(xyzh[0]);
        final int y = Integer.parseInt(xyzh[1]);
        final int z = Integer.parseInt(xyzh[2]);
        final int h = xyzh.length < 4 ? 0 : Integer.parseInt(xyzh[3]);
        return new Location(x, y, z, h);
    }

    public static Location parse(final Element element) {
        final int x = Integer.parseInt(element.getAttributeValue("x"));
        final int y = Integer.parseInt(element.getAttributeValue("y"));
        final int z = Integer.parseInt(element.getAttributeValue("z"));
        final int h = element.getAttributeValue("h") == null ? 0 : Integer.parseInt(element.getAttributeValue("h"));
        return new Location(x, y, z, h);
    }

    /**
     * Найти стабильную точку перед объектом obj1 для спавна объекта obj2, с учетом heading
     *
     * @param x
     * @param y
     * @param z
     * @param radiusmin
     * @param radiusmax
     * @param geoIndex
     * @return
     */
    public static Location findFrontPosition(final GameObject obj, final GameObject obj2, final int radiusmin, final int radiusmax) {
        if (radiusmax == 0 || radiusmax < radiusmin) {
            return new Location(obj);
        }

        final double collision = obj.getColRadius() + obj2.getColRadius();
        int randomRadius, randomAngle, tempz;
        int minangle = 0;
        int maxangle = 360;

        if (obj != obj2) {
            final double angle = PositionUtils.calculateAngleFrom(obj, obj2);
            minangle = (int) angle - 45;
            maxangle = (int) angle + 45;
        }

        final Location pos = new Location();
        for (int i = 0; i < 100; i++) {
            randomRadius = Rnd.get(radiusmin, radiusmax);
            randomAngle = Rnd.get(minangle, maxangle);
            pos.x = obj.getX() + (int) ((collision + randomRadius) * Math.cos(Math.toRadians(randomAngle)));
            pos.y = obj.getY() + (int) ((collision + randomRadius) * Math.sin(Math.toRadians(randomAngle)));
            pos.z = obj.getZ();
            tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, obj.getGeoIndex());
            if (Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, obj.getGeoIndex()) == GeoEngine.NSWE_ALL) {
                pos.z = tempz;
                if (obj != obj2) {
                    pos.h = PositionUtils.getHeadingTo(pos, obj2.getLoc());
                } else {
                    pos.h = obj.getHeading();
                }
                return pos;
            }
        }

        return new Location(obj);
    }

    /**
     * Найти точку в пределах досягаемости от начальной
     *
     * @param x
     * @param y
     * @param z
     * @param radiusmin
     * @param radiusmax
     * @param geoIndex
     * @return
     */
    public static Location findAroundPosition(final int x, final int y, final int z, final int radiusmin, final int radiusmax, final int geoIndex) {
        Location pos;
        int tempz;
        for (int i = 0; i < 100; i++) {
            pos = coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
            tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
            if (GeoEngine.canMoveToCoord(x, y, z, pos.x, pos.y, tempz, geoIndex) && GeoEngine.canMoveToCoord(pos.x, pos.y, tempz, x, y, z, geoIndex)) {
                pos.z = tempz;
                return pos;
            }
        }
        return new Location(x, y, z);
    }

    public static Location findAroundPosition(final Location loc, final int radius, final int geoIndex) {
        return findAroundPosition(loc.x, loc.y, loc.z, 0, radius, geoIndex);
    }

    public static Location findAroundPosition(final Location loc, final int radiusmin, final int radiusmax, final int geoIndex) {
        return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
    }

    public static Location findAroundPosition(final GameObject obj, final Location loc, final int radiusmin, final int radiusmax) {
        return findAroundPosition(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
    }

    public static Location findAroundPosition(final GameObject obj, final int radiusmin, final int radiusmax) {
        return findAroundPosition(obj, obj.getLoc(), radiusmin, radiusmax);
    }

    public static Location findAroundPosition(final GameObject obj, final int radius) {
        return findAroundPosition(obj, 0, radius);
    }

    /**
     * Найти стабильную точку в пределах радиуса от начальной
     *
     * @param x
     * @param y
     * @param z
     * @param radiusmin
     * @param radiusmax
     * @param geoIndex
     * @return
     */
    public static Location findPointToStay(final int x, final int y, final int z, final int radiusmin, final int radiusmax, final int geoIndex) {
        Location pos;
        int tempz;
        for (int i = 0; i < 100; i++) {
            pos = coordsRandomize(x, y, z, 0, radiusmin, radiusmax);
            tempz = GeoEngine.getHeight(pos.x, pos.y, pos.z, geoIndex);
            if (Math.abs(pos.z - tempz) < 200 && GeoEngine.getNSWE(pos.x, pos.y, tempz, geoIndex) == GeoEngine.NSWE_ALL) {
                pos.z = tempz;
                return pos;
            }
        }
        return new Location(x, y, z);
    }

    public static Location findPointToStay(final Location loc, final int radius, final int geoIndex) {
        return findPointToStay(loc.x, loc.y, loc.z, 0, radius, geoIndex);
    }

    public static Location findPointToStay(final Location loc, final int radiusmin, final int radiusmax, final int geoIndex) {
        return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, geoIndex);
    }

    public static Location findPointToStay(final GameObject obj, final Location loc, final int radiusmin, final int radiusmax) {
        return findPointToStay(loc.x, loc.y, loc.z, radiusmin, radiusmax, obj.getGeoIndex());
    }

    public static Location findPointToStay(final GameObject obj, final int radiusmin, final int radiusmax) {
        return findPointToStay(obj, obj.getLoc(), radiusmin, radiusmax);
    }

    public static Location findPointToStay(final GameObject obj, final int radius) {
        return findPointToStay(obj, 0, radius);
    }

    public static Location coordsRandomize(final Location loc, final int radiusmin, final int radiusmax) {
        return coordsRandomize(loc.x, loc.y, loc.z, loc.h, radiusmin, radiusmax);
    }

    public static Location coordsRandomize(final int x, final int y, final int z, final int heading, final int radiusmin, final int radiusmax) {
        if (radiusmax == 0 || radiusmax < radiusmin) {
            return new Location(x, y, z, heading);
        }
        final int radius = Rnd.get(radiusmin, radiusmax);
        final double angle = Rnd.nextDouble() * 2 * Math.PI;
        return new Location((int) (x + radius * Math.cos(angle)), (int) (y + radius * Math.sin(angle)), z, heading);
    }

    public static Point2D findNearest(final Creature creature, final List<Point2D> points) {
        Point2D result = null;
        for (final Point2D point : points) {
            if (result == null)
                result = point;
            else if (creature.getDistance(point.getX(), point.getY()) < creature.getDistance(result.getX(), result.getY()))
                result = point;
        }
        return result;
    }

    public static Location findNearest(final Creature creature, final Location[] locs) {
        Location defloc = null;
        for (final Location loc : locs) {
            if (defloc == null) {
                defloc = loc;
            } else if (creature.getDistance(loc) < creature.getDistance(defloc)) {
                defloc = loc;
            }
        }
        return defloc;
    }

    public static Location findFurther(final Creature creature, final Location[] locs) {
        Location defloc = null;
        for (final Location loc : locs) {
            if (defloc == null) {
                defloc = loc;
            } else if (creature.getDistance(loc) > creature.getDistance(defloc)) {
                defloc = loc;
            }
        }
        return defloc;
    }

    public static int getRandomHeading() {
        return Rnd.get(65535);
    }

    public Location changeZ(final int zDiff) {
        z += zDiff;
        return this;
    }

    public Location correctGeoZ() {
        z = GeoEngine.getHeight(x, y, z, 0);
        return this;
    }

    public Location correctGeoZ(final int refIndex) {
        z = GeoEngine.getHeight(x, y, z, refIndex);
        return this;
    }

    public Location setX(final int x) {
        this.x = x;
        return this;
    }

    public Location setY(final int y) {
        this.y = y;
        return this;
    }

    public Location setZ(final int z) {
        this.z = z;
        return this;
    }

    public Location setH(final int h) {
        this.h = h;
        return this;
    }

    public Location set(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Location set(final int x, final int y, final int z, final int h) {
        set(x, y, z);
        this.h = h;
        return this;
    }

    public Location set(final Location loc) {
        x = loc.x;
        y = loc.y;
        z = loc.z;
        h = loc.h;
        return this;
    }

    public Location world2geo() {
        x = x - World.MAP_MIN_X >> 4;
        y = y - World.MAP_MIN_Y >> 4;
        return this;
    }

    public Location geo2world() {
        // размер одного блока 16*16 точек, +8*+8 это его средина
        x = (x << 4) + World.MAP_MIN_X + 8;
        y = (y << 4) + World.MAP_MIN_Y + 8;
        return this;
    }

    public double distance(final Location loc) {
        return distance(loc.x, loc.y);
    }

    public double distance(final int x, final int y) {
        final long dx = this.x - x;
        final long dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distance3D(final Location loc) {
        return distance3D(loc.x, loc.y, loc.z);
    }

    public double distance3D(final int x, final int y, final int z) {
        final long dx = this.x - x;
        final long dy = this.y - y;
        final long dz = this.z - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public Location clone() {
        return new Location(x, y, z, h);
    }

    @Override
    public final String toString() {
        return x + "," + y + ',' + z + ',' + h;
    }

    public boolean isNull() {
        return x == 0 || y == 0 || z == 0;
    }

    public final String toXYZString() {
        return x + " " + y + ' ' + z;
    }

    @Override
    public Location getRandomLoc(final int ref) {
        return this;
    }
}