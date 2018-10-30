package org.mmocore.gameserver.data.xml.parser;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.commons.geometry.Rectangle;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.templates.mapregion.RestartArea;
import org.mmocore.gameserver.templates.mapregion.RestartPoint;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.io.File;
import java.util.*;

public class RestartPointParser extends AbstractFileParser<MapRegionManager> {
    private static final RestartPointParser _instance = new RestartPointParser();

    private RestartPointParser() {
        super(MapRegionManager.getInstance());
    }

    public static RestartPointParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/mapregion/restart_points.xml");
    }

    @Override
    public String getDTDFileName() {
        return "restart_points.dtd";
    }

    @Override
    protected void readData(final MapRegionManager holder, final Element rootElement) throws Exception {
        final List<Pair<Territory, Map<PlayerRace, String>>> restartArea = new ArrayList<>();
        final Map<String, RestartPoint> restartPoint = new HashMap<>();

        for (final Element listElement : rootElement.getChildren()) {
            if ("restart_area".equals(listElement.getName())) {
                Territory territory = null;
                final Map<PlayerRace, String> restarts = new EnumMap<>(PlayerRace.class);

                for (final Element n : listElement.getChildren()) {
                    if ("region".equalsIgnoreCase(n.getName())) {
                        final Rectangle shape;

                        final Attribute map = n.getAttribute("map");
                        final String s = map.getValue();
                        final String[] val = s.split("_");
                        final int rx = Integer.parseInt(val[0]);
                        final int ry = Integer.parseInt(val[1]);

                        final int x1 = World.MAP_MIN_X + (rx - GeodataConfig.GEO_X_FIRST << 15);
                        final int y1 = World.MAP_MIN_Y + (ry - GeodataConfig.GEO_Y_FIRST << 15);
                        final int x2 = x1 + (1 << 15) - 1;
                        final int y2 = y1 + (1 << 15) - 1;

                        shape = new Rectangle(x1, y1, x2, y2);
                        shape.setZmin(World.MAP_MIN_Z);
                        shape.setZmax(World.MAP_MAX_Z);

                        if (territory == null) {
                            territory = new Territory();
                        }

                        territory.add(shape);
                    } else if ("polygon".equalsIgnoreCase(n.getName())) {
                        final CustomPolygon shape = ZoneParser.parsePolygon(n);

                        if (!shape.validate()) {
                            error("RestartPointParser: invalid territory data : " + shape + '!');
                        }

                        if (territory == null) {
                            territory = new Territory();
                        }

                        territory.add(shape);
                    } else if ("restart".equalsIgnoreCase(n.getName())) {
                        final PlayerRace race = PlayerRace.valueOf(n.getAttributeValue("race"));
                        final String locName = n.getAttributeValue("loc");
                        restarts.put(race, locName);
                    }
                }

                if (territory == null) {
                    throw new RuntimeException("RestartPointParser: empty territory!");
                }

                if (restarts.isEmpty()) {
                    throw new RuntimeException("RestartPointParser: restarts not defined!");
                }

                restartArea.add(new ImmutablePair<>(territory, restarts));
            } else if ("restart_loc".equals(listElement.getName())) {
                final String name = listElement.getAttributeValue("name");
                final int bbs = Integer.parseInt(listElement.getAttributeValue("bbs", "0"));
                final int msgId = Integer.parseInt(listElement.getAttributeValue("msg_id", "0"));
                final List<Location> restartPoints = new ArrayList<>();
                List<Location> PKrestartPoints = new ArrayList<>();

                for (final Element n : listElement.getChildren()) {
                    if ("restart_point".equals(n.getName())) {
                        for (final Element d : n.getChildren("coords")) {
                            final Location loc = Location.parseLoc(d.getAttributeValue("loc"));
                            restartPoints.add(loc);
                        }
                    } else if ("PKrestart_point".equals(n.getName())) {
                        for (final Element d : n.getChildren("coords")) {
                            final Location loc = Location.parseLoc(d.getAttributeValue("loc"));
                            PKrestartPoints.add(loc);
                        }
                    }
                }

                if (restartPoints.isEmpty()) {
                    throw new RuntimeException("RestartPointParser: restart_points not defined for restart_loc : " + name + '!');
                }

                if (PKrestartPoints.isEmpty()) {
                    PKrestartPoints = restartPoints;
                }

                final RestartPoint rp = new RestartPoint(name, bbs, msgId, restartPoints, PKrestartPoints);
                restartPoint.put(name, rp);
            }
        }

        for (final Pair<Territory, Map<PlayerRace, String>> ra : restartArea) {
            final Map<PlayerRace, RestartPoint> restarts = new EnumMap<>(PlayerRace.class);

            for (final Map.Entry<PlayerRace, String> e : ra.getValue().entrySet()) {
                final RestartPoint rp = restartPoint.get(e.getValue());
                if (rp == null) {
                    throw new RuntimeException("RestartPointParser: restart_loc not found : " + e.getValue() + '!');
                }

                restarts.put(e.getKey(), rp);

                holder.addRegionData(new RestartArea(ra.getKey(), restarts));
            }
        }
    }
}
