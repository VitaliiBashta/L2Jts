package org.mmocore.gameserver.templates;

import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.utils.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Laky
 * Date: 16.03.12
 * Time: 23:43
 * The Abyss (c) 2012
 */
public class FenceTemplate extends CharTemplate {
    private final boolean targetable;
    private final CustomPolygon polygon;
    private final Location loc;

    public FenceTemplate(final Location loc, final int type, final int width, final int height) {
        super(CharTemplate.getEmptyStatsSet());
        targetable = false;
        this.loc = loc;
        polygon = new CustomPolygon(4); // <- TODO
        final int x0 = loc.getX();
        final int y0 = loc.getY();
        polygon.add(x0 - width / 2, y0 - height / 2);
        polygon.add(x0 + width / 2, y0 + height / 2);
        polygon.add(x0 - width / 2, y0 - height / 2);
        polygon.add(x0 + width / 2, y0 + height / 2);
        polygon.setZmin(loc.getZ());
        polygon.setZmax(loc.getZ());
    }

    public CustomPolygon getPolygon() {
        return polygon;
    }

    public Location getLoc() {
        return loc;
    }

    public boolean isTargetable() {
        return targetable;
    }
}
