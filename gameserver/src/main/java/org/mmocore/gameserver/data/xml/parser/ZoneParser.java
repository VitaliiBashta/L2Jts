package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.commons.geometry.Circle;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.commons.geometry.Rectangle;
import org.mmocore.commons.geometry.Shape;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ZoneHolder;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author G1ta0
 */
public class ZoneParser extends AbstractDirParser<ZoneHolder> {
    protected ZoneParser() {
        super(ZoneHolder.getInstance());
    }

    public static ZoneParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static Rectangle parseRectangle(final Element n) throws Exception {
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        int zmin = World.MAP_MIN_Z;
        int zmax = World.MAP_MAX_Z;

        final Iterator<Element> i = n.getChildren().iterator();

        Element d = i.next();
        String[] coord = d.getAttributeValue("loc").split("[\\s,;]+");
        x1 = Integer.parseInt(coord[0]);
        y1 = Integer.parseInt(coord[1]);
        if (coord.length > 2) {
            zmin = Integer.parseInt(coord[2]);
            zmax = Integer.parseInt(coord[3]);
        }

        d = i.next();
        coord = d.getAttributeValue("loc").split("[\\s,;]+");
        x2 = Integer.parseInt(coord[0]);
        y2 = Integer.parseInt(coord[1]);
        if (coord.length > 2) {
            zmin = Integer.parseInt(coord[2]);
            zmax = Integer.parseInt(coord[3]);
        }

        final Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
        rectangle.setZmin(zmin);
        rectangle.setZmax(zmax);

        return rectangle;
    }

    public static CustomPolygon parsePolygon(final Element shape) throws Exception {
        final List<Element> shapeChildren = shape.getChildren("coords");

        final CustomPolygon poly = new CustomPolygon(shapeChildren.size());
        for (final Element d : shapeChildren) {
            final String[] coord = d.getAttributeValue("loc").split("[\\s,;]+");
            if (coord.length < 4) // Не указаны minZ и maxZ, берем граничные значения
            {
                poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
            } else {
                poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(Integer.parseInt(coord[2])).setZmax(Integer.parseInt(
                        coord[3]));
            }
        }

        return poly;
    }

    public static Circle parseCircle(final Element shape) throws Exception {
        final Circle circle;

        final String[] coord = shape.getAttributeValue("loc").split("[\\s,;]+");
        if (coord.length < 5) // Не указаны minZ и maxZ, берем граничные значения
        {
            circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(World.MAP_MIN_Z).setZmax(
                    World.MAP_MAX_Z);
        } else {
            circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(Integer.parseInt(
                    coord[3])).setZmax(Integer.parseInt(coord[4]));
        }

        return circle;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/zone/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "zone.dtd";
    }

    @Override
    protected void readData(final ZoneHolder holder, final Element rootElement) throws Exception {
        for (final Element zoneElement : rootElement.getChildren("zone")) {
            final StatsSet zoneDat = new StatsSet();

            zoneDat.set("name", zoneElement.getAttributeValue("name"));
            zoneDat.set("type", zoneElement.getAttributeValue("type"));

            Territory territory = null;
            boolean isShape;

            for (final Element n : zoneElement.getChildren()) {
                if ("set".equals(n.getName())) {
                    zoneDat.set(n.getAttributeValue("name"), n.getAttributeValue("val"));
                } else if ("restart_point".equals(n.getName())) {
                    final List<Location> restartPoints = new ArrayList<>();
                    for (final Element d : n.getChildren("coords")) {
                        final Location loc = Location.parseLoc(d.getAttributeValue("loc"));
                        restartPoints.add(loc);
                    }
                    zoneDat.set("restart_points", restartPoints);
                } else if ("PKrestart_point".equals(n.getName())) {
                    final List<Location> PKrestartPoints = new ArrayList<>();
                    for (final Element d : n.getChildren("coords")) {
                        final Location loc = Location.parseLoc(d.getAttributeValue("loc"));
                        PKrestartPoints.add(loc);
                    }
                    zoneDat.set("PKrestart_points", PKrestartPoints);
                } else if ((isShape = "rectangle".equalsIgnoreCase(n.getName())) || "banned_rectangle".equalsIgnoreCase(n.getName())) {
                    final Shape shape = parseRectangle(n);

                    if (territory == null) {
                        territory = new Territory();
                        zoneDat.set("territory", territory);
                    }

                    if (isShape) {
                        territory.add(shape);
                    } else {
                        territory.addBanned(shape);
                    }
                } else if ((isShape = "circle".equalsIgnoreCase(n.getName())) || "banned_cicrcle".equalsIgnoreCase(n.getName())) {
                    final Shape shape = parseCircle(n);

                    if (territory == null) {
                        territory = new Territory();
                        zoneDat.set("territory", territory);
                    }

                    if (isShape) {
                        territory.add(shape);
                    } else {
                        territory.addBanned(shape);
                    }
                } else if ((isShape = "polygon".equalsIgnoreCase(n.getName())) || "banned_polygon".equalsIgnoreCase(n.getName())) {
                    final CustomPolygon shape = parsePolygon(n);

                    if (!shape.validate()) {
                        error("ZoneParser: invalid territory data : " + shape + ", zone: " + zoneDat.getString("name") + '!');
                    }

                    if (territory == null) {
                        territory = new Territory();
                        zoneDat.set("territory", territory);
                    }

                    if (isShape) {
                        territory.add(shape);
                    } else {
                        territory.addBanned(shape);
                    }
                }
            }

            if (territory == null || territory.getTerritories().isEmpty()) {
                error("Empty territory for zone: " + zoneDat.get("name"));
            }
            final ZoneTemplate template = new ZoneTemplate(zoneDat);
            holder.addTemplate(template);
        }
    }

    private static class LazyHolder {
        private static final ZoneParser INSTANCE = new ZoneParser();
    }
}