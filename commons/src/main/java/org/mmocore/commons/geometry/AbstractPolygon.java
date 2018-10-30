package org.mmocore.commons.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Java-man
 */
public abstract class AbstractPolygon extends AbstractShape {
    /**
     * TWO_PI is a mathematical constant with the value 6.28318530717958647693.
     * It is twice the ratio of the circumference of a circle to its diameter.
     * It is useful in combination with the trigonometric functions
     * <b>sin()</b> and <b>cos()</b>.
     */
    public static final double TWO_PI = 2.0 * Math.PI;

    protected final List<Point2D> pointList;

    protected AbstractPolygon(final List<Point2D> pointList) {
        this.pointList = pointList;
    }

    protected AbstractPolygon(final int numVertices, final Point2D center, final int radius) {
        this.pointList = calculateVertices(numVertices, center, radius);
    }

    /**
     * Calculate the coordinates of the vertices
     */
    protected List<Point2D> calculateVertices(final int numVertices, final Point2D center, final int radius) {
        final double a = TWO_PI / numVertices;

        final List<Point2D> result = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            final double x = Math.cos(i * a) * radius;
            final double y = Math.sin(i * a) * radius;
            result.add(new Point2D((int) x + center.getX(), (int) y + center.getY()));
        }
        return result;
    }

    public List<Point2D> getVertices() {
        return Collections.unmodifiableList(pointList);
    }

    @Override
    public AbstractPolygon setZmax(final int z) {
        max.z = z;
        return this;
    }

    @Override
    public AbstractPolygon setZmin(final int z) {
        min.z = z;
        return this;
    }

    @Override
    public boolean isInside(final int x, final int y) {
        if (x < min.x || x > max.x || y < min.y || y > max.y) {
            return false;
        }

        int hits = 0;
        final int npoints = pointList.size();
        if (npoints == 0)
            return false;
        Point2D last = pointList.get(npoints - 1);

        Point2D cur;
        for (int i = 0; i < npoints; last = cur, i++) {
            cur = pointList.get(i);

            if (cur.y == last.y) {
                continue;
            }

            final int leftx;
            if (cur.x < last.x) {
                if (x >= last.x) {
                    continue;
                }
                leftx = cur.x;
            } else {
                if (x >= cur.x) {
                    continue;
                }
                leftx = last.x;
            }

            final double test1;
            final double test2;
            if (cur.y < last.y) {
                if (y < cur.y || y >= last.y) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - cur.x;
                test2 = y - cur.y;
            } else {
                if (y < last.y || y >= cur.y) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - last.x;
                test2 = y - last.y;
            }

            if (test1 < (test2 / (last.y - cur.y) * (last.x - cur.x))) {
                hits++;
            }
        }

        return (hits & 1) != 0;
    }

    /**
     * Проверяет полигон на самопересечение.
     */
    public boolean validate() {
        if (pointList.size() < 3) {
            return false;
        }

        // треугольник не может быть самопересекающимся
        if (pointList.size() > 3)
        // внешний цикл - перебираем все грани многоугольника
        {
            for (int i = 1; i < pointList.size(); i++) {
                final int ii = i + 1 < pointList.size() ? i + 1 : 0; // вторая точка первой линии
                // внутренний цикл - перебираем все грани многоугольниках кроме той, что во внешнем цикле и соседних
                for (int n = i; n < pointList.size(); n++) {
                    if (Math.abs(n - i) > 1) {
                        final int nn = n + 1 < pointList.size() ? n + 1 : 0; // вторая точка второй линии
                        if (GeometryUtils.checkIfLineSegementsIntersects(pointList.get(i), pointList.get(ii), pointList.get(n), pointList.get(nn))) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < pointList.size(); i++) {
            sb.append(pointList.get(i));
            if (i < pointList.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
