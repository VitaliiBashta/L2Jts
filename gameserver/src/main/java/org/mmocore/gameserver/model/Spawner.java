package org.mmocore.gameserver.model;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.EventOwner;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;
import org.mmocore.gameserver.taskmanager.SpawnTaskManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author VISTALL
 * @date 5:49/19.05.2011
 */
public abstract class Spawner extends EventOwner implements Cloneable {
    protected static final Logger _log = LoggerFactory.getLogger(Spawner.class);

    protected static final int MIN_RESPAWN_DELAY = 20;

    protected int _maximumCount;
    protected int _referenceCount;
    protected int _currentCount;
    protected int _scheduledCount;

    protected int _respawnDelay, _respawnDelayRandom, _nativeRespawnDelay;

    protected int _respawnTime;

    protected boolean _doRespawn;

    protected NpcInstance _lastSpawn;

    protected List<NpcInstance> _spawned;

    protected Reflection _reflection = ReflectionManager.DEFAULT;

    public void decreaseScheduledCount() {
        if (_scheduledCount > 0) {
            _scheduledCount--;
        }
    }

    public boolean isDoRespawn() {
        return _doRespawn;
    }

    public Reflection getReflection() {
        return _reflection;
    }

    public void setReflection(final Reflection reflection) {
        _reflection = reflection;
    }

    public int getRespawnDelay() {
        return _respawnDelay;
    }

    public void setRespawnDelay(final int respawnDelay) {
        setRespawnDelay(respawnDelay, 0);
    }

    public int getNativeRespawnDelay() {
        return _nativeRespawnDelay;
    }

    public int getRespawnDelayRandom() {
        return _respawnDelayRandom;
    }

    public int getRespawnDelayWithRnd() {
        return _respawnDelayRandom == 0 ? _respawnDelay : Rnd.get(_respawnDelay - _respawnDelayRandom, _respawnDelay + _respawnDelayRandom);
    }

    public int getRespawnTime() {
        return _respawnTime;
    }

    public void setRespawnTime(final int respawnTime) {
        _respawnTime = respawnTime;
    }

    public NpcInstance getLastSpawn() {
        return _lastSpawn;
    }

    public void setAmount(final int amount) {
        if (_referenceCount == 0) {
            _referenceCount = amount;
        }
        _maximumCount = amount;
    }

    public void deleteAll() {
        stopRespawn();
        _spawned.forEach(NpcInstance::deleteMe);
        _spawned.clear();
        _respawnTime = 0;
        _scheduledCount = 0;
        _currentCount = 0;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    public abstract void decreaseCount(NpcInstance oldNpc);

    public abstract NpcInstance doSpawn(boolean spawn);

    public abstract void respawnNpc(NpcInstance oldNpc);

    protected abstract NpcInstance initNpc(NpcInstance mob, boolean spawn, MultiValueSet<String> set);

    public abstract int getCurrentNpcId();

    public abstract SpawnRange getCurrentSpawnRange();

    public abstract SuperPoint getSuperPoint();

    //-----------------------------------------------------------------------------------------------------------------------------------
    public int init() {
        while (_currentCount + _scheduledCount < _maximumCount) {
            doSpawn(false);
        }

        _doRespawn = true;

        return _currentCount;
    }

    public NpcInstance initSingle() {
        NpcInstance instance = doSpawn(false);
        _doRespawn = true;
        return instance;
    }

    public NpcInstance spawnOne() {
        return doSpawn(false);
    }

    public void stopRespawn() {
        _doRespawn = false;
    }

    public void startRespawn() {
        _doRespawn = true;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    public List<NpcInstance> getAllSpawned() {
        return _spawned;
    }

    public NpcInstance getFirstSpawned() {
        final List<NpcInstance> npcs = getAllSpawned();
        return !npcs.isEmpty() ? npcs.get(0) : null;
    }

    public void setRespawnDelay(final int respawnDelay, final int respawnDelayRandom) {
        if (respawnDelay < 0) {
            _log.warn("respawn delay is negative");
        }

        _nativeRespawnDelay = respawnDelay;
        _respawnDelay = respawnDelay;
        _respawnDelayRandom = respawnDelayRandom;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    protected NpcInstance doSpawn0(final NpcTemplate template, boolean spawn, final MultiValueSet<String> set) {
        if (template.isInstanceOf(PetInstance.class)) {
            _currentCount++;
            return null;
        }

        final NpcInstance tmp = template.getNewInstance();
        if (tmp == null) {
            return null;
        }

        if (!spawn) {
            spawn = _respawnTime <= System.currentTimeMillis() / 1000 + MIN_RESPAWN_DELAY;
        }

        return initNpc(tmp, spawn, set);
    }

    protected NpcInstance initNpc0(final NpcInstance mob, final Location newLoc, final boolean spawn, final MultiValueSet<String> set) {
        //Синхронизируем Минионов спавн + темплейт
        String Privates = mob.getParameter("Privates", null);
        String Privates_set = set.getString("Privates", null);
        if (Privates != null && Privates_set != null) {
            if (!Privates.endsWith(";")) {
                Privates += ";";
            }
            if (!Privates_set.endsWith(";")) {
                Privates_set += ";";
            }
            if (Privates.equalsIgnoreCase(Privates_set)) {
                mob.removeParameter("Privates");
            } else {
                final String new_privates = Privates + Privates_set;
                mob.removeParameter("Privates");
                set.remove("Privates");
                set.set("Privates", new_privates);
            }
        }

        mob.setParameters(set);

        // Set the HP and MP of the L2NpcInstance to the max
        mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp(), true);

        // Link the L2NpcInstance to this L2Spawn
        mob.setSpawn(this);

        // save spawned points
        mob.setSpawnedLoc(newLoc);

        // Является ли моб "подземным" мобом?
        mob.setUnderground(GeoEngine.getHeight(newLoc, getReflection().getGeoIndex()) < GeoEngine.getHeight(newLoc.clone().changeZ(5000), getReflection().getGeoIndex()));

        getEvents().forEach(mob::addEvent);

        if (spawn) {
            // Спавнится в указанном отражении
            mob.setReflection(getReflection());

            if (mob.isMonster()) {
                ((MonsterInstance) mob).setChampion();
            }

            // Init other values of the L2NpcInstance (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world as a visible object
            mob.spawnMe(newLoc);
            // Increase the current number of L2NpcInstance managed by this L2Spawn
            _currentCount++;
        } else {
            mob.setLoc(newLoc);

            SpawnTaskManager.getInstance().addSpawnTask(mob, _respawnTime * 1000L - System.currentTimeMillis());
            // Update the current number of SpawnTask in progress or stand by of this L2Spawn
            _scheduledCount++;
        }

        _spawned.add(mob);
        _lastSpawn = mob;
        return mob;
    }

    public void decreaseCount0(final NpcTemplate template, final NpcInstance spawnedNpc, final long deadTime) {
        _currentCount--;

        if (_currentCount < 0) {
            _currentCount = 0;
        }

        if (_respawnDelay == 0) {
            return;
        }

        if (_doRespawn && _scheduledCount + _currentCount < _maximumCount) {
            // Update the current number of SpawnTask in progress or stand by of this L2Spawn
            _scheduledCount++;

            long delay = (long) (template.isRaid ? AllSettingsConfig.ALT_RAID_RESPAWN_MULTIPLIER * getRespawnDelayWithRnd() : getRespawnDelayWithRnd()) * 1000L;
            delay = Math.max(1000, delay - deadTime);

            _respawnTime = (int) ((System.currentTimeMillis() + delay) / 1000);

            SpawnTaskManager.getInstance().addSpawnTask(spawnedNpc, delay);
        }
    }
}
