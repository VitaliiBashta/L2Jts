package org.mmocore.gameserver.model;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.spawn.SpawnNpcInfo;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author VISTALL
 * @date 4:58/19.05.2011
 */
public class HardSpawner extends Spawner {
    private final Map<Integer, Queue<NpcInstance>> cache = new ConcurrentHashMap<>();

    private final SpawnTemplate template;
    private final AtomicInteger pointIndex = new AtomicInteger();
    private final AtomicInteger npcIndex = new AtomicInteger();

    private final List<NpcInstance> reSpawned = new CopyOnWriteArrayList<>();

    public HardSpawner(final SpawnTemplate template) {
        this.template = template;
        _spawned = new CopyOnWriteArrayList<>();
    }

    @Override
    public void decreaseCount(final NpcInstance oldNpc) {
        addToCache(oldNpc);

        _spawned.remove(oldNpc);

        final SpawnNpcInfo npcInfo = getNextNpcInfo();

        NpcInstance npc = getCachedNpc(npcInfo.getTemplate().getNpcId());
        if (npc == null) {
            npc = npcInfo.getTemplate().getNewInstance();
        } else {
            npc.refreshID();
        }

        npc.setSpawn(this);

        reSpawned.add(npc);

        decreaseCount0(npcInfo.getTemplate(), npc, oldNpc.getDeadTime());
    }

    @Override
    public NpcInstance doSpawn(final boolean spawn) {
        final SpawnNpcInfo npcInfo = getNextNpcInfo();
        return doSpawn0(npcInfo.getTemplate(), spawn, npcInfo.getParameters());
    }

    @Override
    protected NpcInstance initNpc(final NpcInstance mob, final boolean spawn, final MultiValueSet<String> set) {
        reSpawned.remove(mob);
        final SpawnRange range = template.getSpawnRange(getNextRangeId());
        mob.setSpawnRange(range);
        return initNpc0(mob, range.getRandomLoc(getReflection().getGeoIndex()), spawn, set);
    }

    @Override
    public int getCurrentNpcId() {
        final SpawnNpcInfo npcInfo = template.getNpcInfo(npcIndex.get());
        return npcInfo.getTemplate().npcId;
    }

    @Override
    public SpawnRange getCurrentSpawnRange() {
        return template.getSpawnRange(pointIndex.get());
    }

    @Override
    public SuperPoint getSuperPoint() {
        return template.getSuperPoint();
    }

    @Override
    public void respawnNpc(final NpcInstance oldNpc) {
        final SpawnNpcInfo npcInfo = template.getNpcInfo(npcIndex.get());
        initNpc(oldNpc, true, npcInfo != null ? npcInfo.getParameters() : StatsSet.EMPTY);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();

        for (final NpcInstance npc : reSpawned)
            addToCache(npc);

        reSpawned.clear();

        for (final Queue<NpcInstance> c : cache.values())
            c.clear();

        cache.clear();
    }

    private SpawnNpcInfo getNextNpcInfo() {
        final int old = npcIndex.getAndIncrement();
        if (npcIndex.get() >= template.getNpcSize()) {
            npcIndex.set(0);
        }

        final SpawnNpcInfo npcInfo = template.getNpcInfo(old);
        if (npcInfo.getMax() > 0) {
            int count = 0;
            for (final NpcInstance npc : _spawned) {
                if (npc.getNpcId() == npcInfo.getTemplate().getNpcId()) {
                    count++;
                }
            }

            if (count >= npcInfo.getMax()) {
                return getNextNpcInfo();
            }
        }
        return npcInfo;
    }

    private int getNextRangeId() {
        final int old = pointIndex.getAndIncrement();

        if (pointIndex.get() >= template.getSpawnRangeSize())
            pointIndex.set(0);

        return old;
    }

    @Override
    public HardSpawner clone() {
        final HardSpawner spawnDat = new HardSpawner(template);
        spawnDat.setAmount(_maximumCount);
        spawnDat.setRespawnDelay(_respawnDelay, _respawnDelayRandom);
        spawnDat.setRespawnTime(0);
        return spawnDat;
    }

    private void addToCache(final NpcInstance npc) {
        npc.setSpawn(null);
        npc.decayMe();

        Queue<NpcInstance> queue = cache.get(npc.getNpcId());
        if (queue == null) {
            cache.put(npc.getNpcId(), queue = new ArrayDeque<>());
        }

        queue.add(npc);
    }

    private NpcInstance getCachedNpc(final int id) {
        final Queue<NpcInstance> queue = cache.get(id);
        if (queue == null) {
            return null;
        }

        final NpcInstance npc = queue.poll();
        if (npc != null && npc.isDeleted()) {
            _log.info("Npc: {} is deleted, cant used by cache.", id);
            return getCachedNpc(id);
        } else {
            return npc;
        }
    }
}