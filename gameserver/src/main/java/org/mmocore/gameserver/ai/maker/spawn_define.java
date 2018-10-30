package org.mmocore.gameserver.ai.maker;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.common.Point4;
import org.jts.dataparser.data.holder.NpcPosHolder;
import org.jts.dataparser.data.holder.npcpos.PosTerritory;
import org.jts.dataparser.data.holder.npcpos.common.DefaultMakerNpc;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KilRoy
 */
public class spawn_define {
    private static final Logger LOGGER = LoggerFactory.getLogger(spawn_define.class);

    private final DefaultMakerNpc ptsMaker;
    private final NpcTemplate template;
    private default_maker maker;
    private AtomicInteger npc_count = new AtomicInteger();
    ;
    private Location spawnLocation = new Location();
    private Territory spawnTerritories = new Territory();
    private String[] territories;
    private String[] banned_territories;
    private HashSet<NpcInstance> spawned_npc;

    public spawn_define(final NpcTemplate template, final DefaultMakerNpc ptsMaker) {
        this.template = template;
        this.ptsMaker = ptsMaker;
        spawned_npc = new HashSet<>(ptsMaker.getTotal());
    }

    public AtomicInteger getNpcCount() {
        return npc_count;
    }

    public int getTotal() {
        return ptsMaker.getTotal();
    }

    public long getRespawnTime() {
        return ptsMaker.getRespawnTime();
    }

    public long getRespawnRandTime() {
        return ptsMaker.getRespawnRandTime();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Location getRandomSpawnLocation(final int geoIndex) {
        return spawnTerritories.getRandomLoc(geoIndex);
    }

    public Location getStartLocation(final NpcInstance npc) {
        if (spawnTerritories == null || spawnTerritories.getTerritories().isEmpty()) {
            return getSpawnLocation();
        } else {
            return getRandomSpawnLocation(npc.getGeoIndex());
        }
    }

    public String[] getTerritories() {
        return territories;
    }

    public String[] getBannedTerritories() {
        return banned_territories;
    }

    public NpcTemplate getTemplate() {
        return template;
    }

    public default_maker getDefaultMaker() {
        return maker;
    }

    public void setDefaultMaker(final default_maker maker) {
        this.maker = maker;
    }

    public void addTerritories(final String[] territories, final String[] banned_territories) {
        this.territories = territories;
        this.banned_territories = banned_territories;
        String location = ptsMaker.getPos();
        if (!"anywhere".equalsIgnoreCase(location)) // TODO[K] - будет временно спавнить в рандомную локацию без учета шанса координат в массиве.
        {
            location = location.replaceAll("\\{", "");
            location = location.replaceAll("};", ":");
            location = location.replaceAll("}", "");
            final String[] parsedLoc = location.split(":");
            if (parsedLoc.length > 1) {
                final String[] s = Rnd.get(parsedLoc).split(";", 4);
                final int x = Integer.parseInt(s[0]);
                final int y = Integer.parseInt(s[1]);
                final int z = Integer.parseInt(s[2]);
                //final int chance = Integer.parseInt(s[3]);
                spawnLocation.set(x, y, z, 0);
            } else {
                spawnLocation.set(Location.parseLoc(parsedLoc[0]));
            }
        } else {
            for (final PosTerritory territory : NpcPosHolder.getInstance().getTerritories()) {
                if (ArrayUtils.contains(territories, territory.getPosTerritoryName())) {
                    final CustomPolygon temp = new CustomPolygon(territory.getPoints().length);
                    for (final Point4 point : territory.getPoints()) {
                        temp.add(point.getX(), point.getY()).setZmin(point.getZMin()).setZmax(point.getZMax());
                    }
                    if (!temp.validate()) {
                        LOGGER.error("Invalid polygon={}", territory.getPosTerritoryName());
                        continue;
                    }
                    spawnTerritories.add(temp);
                } else if (ArrayUtils.contains(banned_territories, territory.getPosTerritoryName())) {
                    final CustomPolygon banTemp = new CustomPolygon(territory.getPoints().length);
                    for (final Point4 point : territory.getPoints()) {
                        banTemp.add(point.getX(), point.getY()).setZmin(point.getZMin()).setZmax(point.getZMax());
                    }
                    if (!banTemp.validate()) {
                        LOGGER.error("Invalid ban polygon={}", territory.getPosTerritoryName());
                        continue;
                    }
                    spawnTerritories.addBanned(banTemp);
                }
            }
        }
    }

    public void spawn(final int defTotal, final long respawnTime, final long respawnRandTime) {
        if (ptsMaker.getDBName() != null && !ptsMaker.getDBName().isEmpty()) {
            //LOGGER.error("Skiped loading |{}| and him maker. Him used DB storage info.", ptsMaker.getNpcName());
            return;
        }
        for (int i0 = 0; i0 < defTotal; i0++) {
            if (template == null) {
                throw new NullPointerException("Npc template in MAKER not loaded!");
            }
            final NpcInstance npc = template.getNewInstance();
            spawned_npc.add(npc);
            final Location loc = getStartLocation(npc);
            if (loc == null) {
                LOGGER.error("NpcMaker={} returned nulled location.", ptsMaker.getNpcName());
                return;
            } else if (loc.getX() == 0 && loc.getY() == 0) {
                LOGGER.info(loc.toXYZString() + " loc for NpcName=" + ptsMaker.getNpcName() + " territory=" + getTerritories()[0]);
            }
            npc.setHeading(loc.h < 0 ? Rnd.get(0xFFFF) : loc.h);
            npc.setSpawnedLoc(loc);
            npc.setReflection(ReflectionManager.DEFAULT); // TODO[K] - инициализация инстанса на спавне с мейкера
            npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
            npc.spawnMe(npc.getSpawnedLoc());
            npc.setDefaultMaker(getDefaultMaker());
            if (ptsMaker.getChasePcRange() > 0) {
                npc.setParameter("MaxPursueRange", ptsMaker.getChasePcRange());
            }
            //if(despawnTime > 0) TODO[K] - respawn!
            //{
            //	ThreadPoolManager.getInstance().schedule(new DeleteTask(npc), despawnTime);
            //}
        }
    }

    public void respawn(final int defTotal, final long respawnTime, final long respawnRandTime) {

    }

    public void despawn(final default_maker maker) {
        for (final NpcInstance npcs : spawned_npc) {
            if (npcs.isVisible()) {
                npcs.deleteMe();
            }
        }
        maker.getMySelf().AtomicDecreaseTotal(getTotal());
        npc_count.set(0);
    }
}