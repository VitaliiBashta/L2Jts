package org.mmocore.commons.geometry;

import java.util.ArrayList;
import java.util.List;

public class CustomPolygon extends AbstractPolygon {
    public CustomPolygon(final List<Point2D> pointList) {
        super(pointList);
    }

    public CustomPolygon(final int numVertices) {
        super(new ArrayList<Point2D>(numVertices));
    }

    public CustomPolygon add(final int x, final int y) {
        add(new Point2D(x, y));
        return this;
    }

    public CustomPolygon add(final Point2D p) {
        if (pointList.isEmpty()) {
            min.y = p.y;
            min.x = p.x;
            max.x = p.x;
            max.y = p.y;
        } else {
            min.y = Math.min(min.y, p.y);
            min.x = Math.min(min.x, p.x);
            max.x = Math.max(max.x, p.x);
            max.y = Math.max(max.y, p.y);
        }
        pointList.add(p);

        return this;
    }

    @Override
    public CustomPolygon setZmax(final int z) {
        max.z = z;
        return this;
    }

    @Override
    public CustomPolygon setZmin(final int z) {
        min.z = z;
        return this;
    }
}
