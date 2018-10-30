package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.DoorHolder;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.data.xml.holder.SpawnHolder;
import org.mmocore.gameserver.data.xml.holder.ZoneHolder;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.templates.DoorTemplate;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.InstantZone.DoorInfo;
import org.mmocore.gameserver.templates.InstantZone.SpawnInfo;
import org.mmocore.gameserver.templates.InstantZone.SpawnInfo2;
import org.mmocore.gameserver.templates.InstantZone.ZoneInfo;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;
import org.mmocore.gameserver.utils.Location;
import org.quartz.CronExpression;

import java.io.File;
import java.util.*;

/**
 * @author VISTALL
 */
public class InstantZoneParser extends AbstractDirParser<InstantZoneHolder> {
    public InstantZoneParser() {
        super(InstantZoneHolder.getInstance());
    }

    public static InstantZoneParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/instances/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "instances.dtd";
    }

    @Override
    protected void readData(final InstantZoneHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            final int instanceId;
            int displayId;
            final String name;
            CronExpression resetReuse = QuartzUtils.createCronExpression("0 30 6 * * ?"); // Сброс реюза по умолчанию в каждые сутки в 6:30
            int timelimit = -1;
            int timer = 60;
            int mapx = -1;
            int mapy = -1;
            boolean dispelBuffs = false;
            boolean onPartyDismiss = true;
            int mobId, respawn, respawnRnd, count, sharedReuseGroup = 0;
            int collapseIfEmpty = 0;
            // 0 - точечный, в каждой указанной точке; 1 - один точечный спаун в рандомной точке; 2 - локационный
            int spawnType = 0;
            SpawnInfo spawnDat = null;
            int removedItemId = 0, removedItemCount = 0, giveItemId = 0, givedItemCount = 0, requiredQuestId = 0;
            int maxChannels = 20;
            boolean removedItemNecessity = false;
            boolean setReuseUponEntry = true;
            final StatsSet params = new StatsSet();

            final ArrayList<SpawnInfo> spawns = new ArrayList<>();
            Map<Integer, DoorInfo> doors = Collections.emptyMap();
            Map<String, ZoneInfo> zones = Collections.emptyMap();
            Map<String, SpawnInfo2> spawns2 = Collections.emptyMap();
            instanceId = Integer.parseInt(element.getAttributeValue("id"));
            name = element.getAttributeValue("name");

            String n = element.getAttributeValue("displayId");
            displayId = n != null ? Integer.parseInt(n) : instanceId;

            n = element.getAttributeValue("timelimit");
            if (n != null) {
                timelimit = Integer.parseInt(n);
            }

            n = element.getAttributeValue("collapseIfEmpty");
            collapseIfEmpty = Integer.parseInt(n);

            n = element.getAttributeValue("maxChannels");
            maxChannels = Integer.parseInt(n);

            n = element.getAttributeValue("dispelBuffs");
            dispelBuffs = n != null && Boolean.parseBoolean(n);

            int minLevel = 0, maxLevel = 0, minParty = 1, maxParty = 9;
            List<Location> teleportLocs = Collections.emptyList();
            Location ret = null;

            for (final Element subElement : element.getChildren()) {
                if ("level".equalsIgnoreCase(subElement.getName())) {
                    minLevel = Integer.parseInt(subElement.getAttributeValue("min"));
                    maxLevel = Integer.parseInt(subElement.getAttributeValue("max"));
                } else if ("collapse".equalsIgnoreCase(subElement.getName())) {
                    onPartyDismiss = Boolean.parseBoolean(subElement.getAttributeValue("on-party-dismiss"));
                    timer = Integer.parseInt(subElement.getAttributeValue("timer"));
                } else if ("party".equalsIgnoreCase(subElement.getName())) {
                    minParty = Integer.parseInt(subElement.getAttributeValue("min"));
                    maxParty = Integer.parseInt(subElement.getAttributeValue("max"));
                } else if ("return".equalsIgnoreCase(subElement.getName())) {
                    ret = Location.parseLoc(subElement.getAttributeValue("loc"));
                } else if ("teleport".equalsIgnoreCase(subElement.getName())) {
                    if (teleportLocs.isEmpty()) {
                        teleportLocs = new ArrayList<>(1);
                    }
                    teleportLocs.add(Location.parseLoc(subElement.getAttributeValue("loc")));
                } else if ("remove".equalsIgnoreCase(subElement.getName())) {
                    removedItemId = Integer.parseInt(subElement.getAttributeValue("itemId"));
                    removedItemCount = Integer.parseInt(subElement.getAttributeValue("count"));
                    removedItemNecessity = Boolean.parseBoolean(subElement.getAttributeValue("necessary"));
                } else if ("give".equalsIgnoreCase(subElement.getName())) {
                    giveItemId = Integer.parseInt(subElement.getAttributeValue("itemId"));
                    givedItemCount = Integer.parseInt(subElement.getAttributeValue("count"));
                } else if ("quest".equalsIgnoreCase(subElement.getName())) {
                    requiredQuestId = Integer.parseInt(subElement.getAttributeValue("id"));
                } else if ("reuse".equalsIgnoreCase(subElement.getName())) {
                    resetReuse = QuartzUtils.createCronExpression(subElement.getAttributeValue("resetReuse"));
                    sharedReuseGroup = Integer.parseInt(subElement.getAttributeValue("sharedReuseGroup"));
                    setReuseUponEntry = Boolean.parseBoolean(subElement.getAttributeValue("setUponEntry"));
                } else if ("geodata".equalsIgnoreCase(subElement.getName())) {
                    final String[] rxy = subElement.getAttributeValue("map").split("_");
                    mapx = Integer.parseInt(rxy[0]);
                    mapy = Integer.parseInt(rxy[1]);
                } else if ("doors".equalsIgnoreCase(subElement.getName())) {
                    for (final Element e : subElement.getChildren()) {
                        if (doors.isEmpty()) {
                            doors = new HashMap<>();
                        }

                        final boolean opened = Boolean.parseBoolean(e.getAttributeValue("opened", "false"));
                        final boolean invul = Boolean.parseBoolean(e.getAttributeValue("invul", "true"));
                        final DoorTemplate template = DoorHolder.getInstance().getTemplate(Integer.parseInt(e.getAttributeValue("id")));
                        doors.put(template.getNpcId(), new DoorInfo(template, opened, invul));
                    }
                } else if ("zones".equalsIgnoreCase(subElement.getName())) {
                    for (final Element e : subElement.getChildren()) {
                        if (zones.isEmpty()) {
                            zones = new HashMap<>();
                        }

                        final boolean active = Boolean.parseBoolean(e.getAttributeValue("active", "false"));
                        final ZoneTemplate template = ZoneHolder.getInstance().getTemplate(e.getAttributeValue("name"));
                        if (template == null) {
                            error("Zone: " + e.getAttributeValue("name") + " not found; file: " + getCurrentFileName());
                            continue;
                        }
                        zones.put(template.getName(), new ZoneInfo(template, active));
                    }
                } else if ("add_parameters".equalsIgnoreCase(subElement.getName())) {
                    for (final Element e : subElement.getChildren("param")) {
                        params.set(e.getAttributeValue("name"), e.getAttributeValue("value"));
                    }
                } else if ("spawns".equalsIgnoreCase(subElement.getName())) {
                    for (final Element e : subElement.getChildren()) {
                        if ("group".equalsIgnoreCase(e.getName())) {
                            final String group = e.getAttributeValue("name");
                            final boolean spawned = e.getAttributeValue("spawned") != null && Boolean.parseBoolean(e.getAttributeValue("spawned"));
                            final List<SpawnTemplate> templates = SpawnHolder.getInstance().getSpawn(group);
                            if (templates == null) {
                                info("not find spawn group: " + group + " in file: " + getCurrentFileName());
                            } else {
                                if (spawns2.isEmpty()) {
                                    spawns2 = new Hashtable<>();
                                }

                                spawns2.put(group, new SpawnInfo2(templates, spawned));
                            }
                        } else if ("spawn".equalsIgnoreCase(e.getName())) {
                            final String[] mobs = e.getAttributeValue("mobId").split(" ");

                            final String respawnNode = e.getAttributeValue("respawn");
                            respawn = respawnNode != null ? Integer.parseInt(respawnNode) : 0;

                            final String respawnRndNode = e.getAttributeValue("respawnRnd");
                            respawnRnd = respawnRndNode != null ? Integer.parseInt(respawnRndNode) : 0;

                            final String countNode = e.getAttributeValue("count");
                            count = countNode != null ? Integer.parseInt(countNode) : 1;

                            final List<Location> coords = new ArrayList<>();
                            spawnType = 0;

                            final String spawnTypeNode = e.getAttributeValue("type");
                            if (spawnTypeNode == null || "point".equalsIgnoreCase(spawnTypeNode)) {
                                spawnType = 0;
                            } else if ("rnd".equalsIgnoreCase(spawnTypeNode)) {
                                spawnType = 1;
                            } else if ("loc".equalsIgnoreCase(spawnTypeNode)) {
                                spawnType = 2;
                            } else {
                                error("Spawn type  '" + spawnTypeNode + "' is unknown!");
                            }

                            for (final Element e2 : e.getChildren("coords")) {
                                coords.add(Location.parseLoc(e2.getAttributeValue("loc")));
                            }

                            Territory territory = null;
                            if (spawnType == 2) {
                                final CustomPolygon poly = new CustomPolygon(coords.size());
                                for (final Location loc : coords) {
                                    poly.add(loc.x, loc.y).setZmin(loc.z).setZmax(loc.z);
                                }

                                if (!poly.validate()) {
                                    error("invalid spawn territory for instance id : " + instanceId + " - " + poly + '!');
                                }

                                territory = new Territory().add(poly);
                            }

                            for (final String mob : mobs) {
                                mobId = Integer.parseInt(mob);
                                spawnDat = new SpawnInfo(spawnType, mobId, count, respawn, respawnRnd, coords, territory);
                                spawns.add(spawnDat);
                            }
                        }
                    }
                }
            }

            final InstantZone instancedZone = new InstantZone(instanceId, displayId, name, resetReuse, sharedReuseGroup, timelimit, dispelBuffs, minLevel, maxLevel,
                    minParty, maxParty, timer, onPartyDismiss, teleportLocs, ret, mapx, mapy, doors, zones,
                    spawns2, spawns, collapseIfEmpty, maxChannels, removedItemId, removedItemCount,
                    removedItemNecessity, giveItemId, givedItemCount, requiredQuestId, setReuseUponEntry, params);
            holder.addInstantZone(instancedZone);
        }
    }

    private static class LazyHolder {
        private static final InstantZoneParser INSTANCE = new InstantZoneParser();
    }
}