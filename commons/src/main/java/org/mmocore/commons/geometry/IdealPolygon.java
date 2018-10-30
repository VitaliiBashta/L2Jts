package org.mmocore.commons.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * С помощью этого класса можно реализовать идеальный многоугольник.
 *
 * @author Java-man
 */
public class IdealPolygon extends AbstractPolygon {
    private IdealPolygon(final List<Point2D> pointList) {
        super(pointList);
    }

    private IdealPolygon(final int numVertices, final Point2D center, final int radius) {
        super(numVertices, center, radius);
    }

    /**
     * Creates a new polygon representing a rectangle centered around a point.
     * Rectangle sides are parallel to the main axes.
     */
    public static IdealPolygon createRectangle(final Point2D center, final int radius) {
        final int xc = center.getX();
        final int yc = center.getY();
        final int len = radius / 2;
        final int wid = radius / 2;

        // coordinates of corners
        final int x1 = xc - len;
        final int y1 = yc - wid;
        final int x2 = xc + len;
        final int y2 = yc + wid;

        // create result polygon
        return new IdealPolygon(Arrays.asList(new Point2D(x1, y1), new Point2D(x2, y1),
                new Point2D(x2, y2), new Point2D(x1, y2)));
    }

    public static IdealPolygon createDecagon(final Point2D center, final int radius) {
        return new IdealPolygon(10, center, radius);
    }
}
