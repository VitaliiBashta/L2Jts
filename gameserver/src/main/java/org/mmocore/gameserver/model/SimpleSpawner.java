package org.mmocore.gameserver.model;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;

@Deprecated
public class SimpleSpawner extends Spawner implements Cloneable {
    private final NpcTemplate _npcTemplate;

    private int _locx, _locy, _locz, _heading;
    private Territory _territory;

    public SimpleSpawner(final NpcTemplate mobTemplate) {
        if (mobTemplate == null) {
            throw new NullPointerException();
        }

        _npcTemplate = mobTemplate;
        _spawned = new ArrayList<>(1);
    }

    public SimpleSpawner(final int npcId) {
        final NpcTemplate mobTemplate = NpcHolder.getInstance().getTemplate(npcId);
        if (mobTemplate == null) {
            throw new NullPointerException("Not find npc: " + npcId);
        }

        _npcTemplate = mobTemplate;
        _spawned = new ArrayList<>(1);
    }

    /**
     * Return the maximum number of L2NpcInstance that this L2Spawn can manage.<BR><BR>
     */
    public int getAmount() {
        return _maximumCount;
    }

    /**
     * Return the number of L2NpcInstance that this L2Spawn spawned.<BR><BR>
     */
    public int getSpawnedCount() {
        return _currentCount;
    }

    /**
     * Return the number of L2NpcInstance that this L2Spawn sheduled.<BR><BR>
     */
    public int getSheduledCount() {
        return _scheduledCount;
    }

    /**
     * Return the Identifier of the location area where L2NpcInstance can be spwaned.<BR><BR>
     */
    public Territory getTerritory() {
        return _territory;
    }

    /**
     * Set the Identifier of the location area where L2NpcInstance can be spawned.<BR><BR>
     */
    public void setTerritory(final Territory territory) {
        _territory = territory;
    }

    /**
     * Return the position of the spawn point.<BR><BR>
     */
    public Location getLoc() {
        return new Location(_locx, _locy, _locz);
    }

    /**
     * Set the position(x, y, z, heading) of the spawn point.
     *
     * @param loc Location
     */
    public void setLoc(final Location loc) {
        _locx = loc.x;
        _locy = loc.y;
        _locz = loc.z;
        _heading = loc.h;
    }

    /**
     * Return the X position of the spawn point.<BR><BR>
     */
    public int getLocx() {
        return _locx;
    }

    /**
     * Set the X position of the spawn point.<BR><BR>
     */
    public void setLocx(final int locx) {
        _locx = locx;
    }

    /**
     * Return the Y position of the spawn point.<BR><BR>
     */
    public int getLocy() {
        return _locy;
    }

    /**
     * Set the Y position of the spawn point.<BR><BR>
     */
    public void setLocy(final int locy) {
        _locy = locy;
    }

    /**
     * Return the Z position of the spawn point.<BR><BR>
     */
    public int getLocz() {
        return _locz;
    }

    /**
     * Set the Z position of the spawn point.<BR><BR>
     */
    public void setLocz(final int locz) {
        _locz = locz;
    }

    /**
     * Return the Identifier of the L2NpcInstance manage by this L2Spwan contained in the L2NpcTemplate.<BR><BR>
     */
    @Override
    public int getCurrentNpcId() {
        return _npcTemplate.getNpcId();
    }

    @Override
    public SpawnRange getCurrentSpawnRange() {
        if (_locx == 0 && _locz == 0) {
            return _territory;
        }
        return getLoc();
    }

    @Override
    public SuperPoint getSuperPoint() {
        return null;
    }

    /**
     * Return the heading of L2NpcInstance when they are spawned.<BR><BR>
     */
    public int getHeading() {
        return _heading;
    }

    /**
     * Set the heading of L2NpcInstance when they are spawned.<BR><BR>
     */
    public void setHeading(final int heading) {
        _heading = heading;
    }

    /**
     * Восстанавливает измененное количество
     */
    public void restoreAmount() {
        _maximumCount = _referenceCount;
    }

    @Override
    public void decreaseCount(final NpcInstance oldNpc) {
        decreaseCount0(_npcTemplate, oldNpc, oldNpc.getDeadTime());
    }

    @Override
    public NpcInstance doSpawn(final boolean spawn) {
        return doSpawn0(_npcTemplate, spawn, StatsSet.EMPTY);
    }

    @Override
    protected NpcInstance initNpc(final NpcInstance mob, final boolean spawn, final MultiValueSet<String> set) {
        final Location newLoc;

        if (_territory != null) {
            newLoc = _territory.getRandomLoc(_reflection.getGeoIndex());
            newLoc.setH(Rnd.get(0xFFFF));
        } else {
            newLoc = getLoc();

            newLoc.h = getHeading() == -1 ? Rnd.get(0xFFFF) : getHeading();
        }

        return initNpc0(mob, newLoc, spawn, set);
    }

    @Override
    public void respawnNpc(final NpcInstance oldNpc) {
        oldNpc.refreshID();
        initNpc(oldNpc, true, StatsSet.EMPTY);
    }

    @Override
    public SimpleSpawner clone() {
        final SimpleSpawner spawnDat = new SimpleSpawner(_npcTemplate);
        spawnDat.setTerritory(_territory);
        spawnDat.setLocx(_locx);
        spawnDat.setLocy(_locy);
        spawnDat.setLocz(_locz);
        spawnDat.setHeading(_heading);
        spawnDat.setAmount(_maximumCount);
        spawnDat.setRespawnDelay(_respawnDelay, _respawnDelayRandom);
        return spawnDat;
    }
}