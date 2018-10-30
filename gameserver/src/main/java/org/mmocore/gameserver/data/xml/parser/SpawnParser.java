package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.SpawnHolder;
import org.mmocore.gameserver.data.xml.holder.SuperPointHolder;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.spawn.PeriodOfDay;
import org.mmocore.gameserver.templates.spawn.SpawnNpcInfo;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;
import org.mmocore.gameserver.utils.Location;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 18:38/10.12.2010
 */
public final class SpawnParser extends AbstractDirParser<SpawnHolder> {
    private SpawnParser() {
        super(SpawnHolder.getInstance());
    }

    public static SpawnParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/spawn/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "spawn.dtd";
    }

    @Override
    protected void readData(final SpawnHolder holder, final Element rootElement) throws Exception {
        final Map<String, Territory> territories = new HashMap<>();

        for (final Element spawnElement : rootElement.getChildren()) {
            if ("territory".equalsIgnoreCase(spawnElement.getName())) {
                final String terName = spawnElement.getAttributeValue("name");
                final Territory territory = parseTerritory(terName, spawnElement);
                territories.put(terName, territory);
            } else if ("spawn".equalsIgnoreCase(spawnElement.getName())) {
                String group = spawnElement.getAttributeValue("group");
                final int respawn = spawnElement.getAttributeValue("respawn") == null ? 60 : Integer.parseInt(spawnElement.getAttributeValue("respawn"));
                final int respawnRandom = spawnElement.getAttributeValue("respawn_random") == null ? 0 : Integer.parseInt(spawnElement.getAttributeValue(
                        "respawn_random"));
                final int count = spawnElement.getAttributeValue("count") == null ? 1 : Integer.parseInt(spawnElement.getAttributeValue("count"));
                final PeriodOfDay periodOfDay = spawnElement.getAttributeValue("period_of_day") == null ? PeriodOfDay.NONE : PeriodOfDay.valueOf(
                        spawnElement.getAttributeValue("period_of_day").toUpperCase());
                if (group == null) {
                    group = periodOfDay.name();
                }
                final SpawnTemplate template = new SpawnTemplate(periodOfDay, count, respawn, respawnRandom);

                for (final Element subElement : spawnElement.getChildren()) {
                    if ("point".equalsIgnoreCase(subElement.getName())) {
                        final int x = Integer.parseInt(subElement.getAttributeValue("x"));
                        final int y = Integer.parseInt(subElement.getAttributeValue("y"));
                        final int z = Integer.parseInt(subElement.getAttributeValue("z"));
                        int h = subElement.getAttributeValue("h") == null ? -1 : Integer.parseInt(subElement.getAttributeValue("h"));
                        final String superPoint = subElement.getAttributeValue("superPoint");

                        if (h < 0 && h != -1) {
                            h &= 0xFFFF;
                        }

                        template.addSpawnRange(new Location(x, y, z, h));
                        if (superPoint != null)
                            template.setSuperPoint(SuperPointHolder.getInstance().getSuperPointsByName(superPoint));
                    } else if ("territory".equalsIgnoreCase(subElement.getName())) {
                        final String terName = subElement.getAttributeValue("name");
                        final String superPoint = subElement.getAttributeValue("superPoint");
                        if (terName != null) {
                            final Territory g = territories.get(terName);
                            if (g == null) {
                                error("Invalid territory name: " + terName + "; " + getCurrentFileName());
                                continue;
                            }
                            template.addSpawnRange(g);
                            if (superPoint != null)
                                template.setSuperPoint(SuperPointHolder.getInstance().getSuperPointsByName(superPoint));
                        } else {
                            final Territory temp = parseTerritory(null, subElement);

                            template.addSpawnRange(temp);
                            if (superPoint != null)
                                template.setSuperPoint(SuperPointHolder.getInstance().getSuperPointsByName(superPoint));
                        }
                    } else if ("npc".equalsIgnoreCase(subElement.getName())) {
                        final int npcId = Integer.parseInt(subElement.getAttributeValue("id"));
                        final int max = subElement.getAttributeValue("max") == null ? 0 : Integer.parseInt(subElement.getAttributeValue("max"));
                        MultiValueSet<String> parameters = StatsSet.EMPTY;
                        for (final Element e : subElement.getChildren()) {
                            if (parameters.isEmpty()) {
                                parameters = new MultiValueSet<>();
                            }

                            parameters.set(e.getAttributeValue("name"), e.getAttributeValue("value"));
                        }
                        template.addNpc(new SpawnNpcInfo(npcId, max, parameters));
                    }
                }

                if (template.getNpcSize() == 0) {
                    warn("Npc id is zero! File: " + getCurrentFileName());
                    continue;
                }

                if (template.getSpawnRangeSize() == 0) {
                    warn("No points to spawn! File: " + getCurrentFileName());
                    continue;
                }

                holder.addSpawn(group, template);
            }
        }
    }

    private Territory parseTerritory(final String name, final Element e) {
        final Territory t = new Territory();
        t.add(parsePolygon0(name, e));

        for (final Element bannedTerritoryElement : e.getChildren("banned_territory")) {
            t.addBanned(parsePolygon0(name, bannedTerritoryElement));
        }

        return t;
    }

    private CustomPolygon parsePolygon0(final String name, final Element e) {
        final List<Element> addChildren = e.getChildren("add");

        final CustomPolygon temp = new CustomPolygon(addChildren.size());
        for (final Element addElement : addChildren) {
            final int x = Integer.parseInt(addElement.getAttributeValue("x"));
            final int y = Integer.parseInt(addElement.getAttributeValue("y"));
            final int zmin = Integer.parseInt(addElement.getAttributeValue("zmin"));
            final int zmax = Integer.parseInt(addElement.getAttributeValue("zmax"));
            temp.add(x, y).setZmin(zmin).setZmax(zmax);
        }

        if (!temp.validate()) {
            error("Invalid polygon: " + name + '{' + temp + "}. File: " + getCurrentFileName());
        }
        return temp;
    }

    private static class LazyHolder {
        private static final SpawnParser INSTANCE = new SpawnParser();
    }
}
