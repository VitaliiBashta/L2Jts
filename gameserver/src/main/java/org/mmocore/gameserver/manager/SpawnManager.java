package org.mmocore.gameserver.manager;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.data.xml.holder.SpawnHolder;
import org.mmocore.gameserver.listener.game.OnDayNightChangeListener;
import org.mmocore.gameserver.model.HardSpawner;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.MonsterWithLongRespawnInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.templates.spawn.PeriodOfDay;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnManager {
    private static final Logger _log = LoggerFactory.getLogger(SpawnManager.class);
    private final Map<String, List<Spawner>> _spawns = new ConcurrentHashMap<>();
    private final Listeners _listeners = new Listeners();
    private SpawnManager() {
        for (final Map.Entry<String, List<SpawnTemplate>> entry : SpawnHolder.getInstance().getSpawns().entrySet()) {
            fillSpawn(entry.getKey(), entry.getValue());
        }

        GameTimeManager.getInstance().addListener(_listeners);
    }

    public static SpawnManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<Spawner> fillSpawn(final String group, final List<SpawnTemplate> templateList) {
        if (ServerConfig.DONTLOADSPAWN) {
            return Collections.emptyList();
        }

        List<Spawner> spawnerList = _spawns.get(group);
        if (spawnerList == null) {
            _spawns.put(group, spawnerList = new ArrayList<>(templateList.size()));
        }

        for (final SpawnTemplate template : templateList) {
            final Spawner spawner = new HardSpawner(template);
            spawnerList.add(spawner);

            final NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(spawner.getCurrentNpcId());

            if (ServerConfig.RATE_MOB_SPAWN > 1 && npcTemplate.getInstanceClass().equals(MonsterInstance.class) && npcTemplate.level >= ServerConfig.RATE_MOB_SPAWN_MIN_LEVEL && npcTemplate.level <= ServerConfig.RATE_MOB_SPAWN_MAX_LEVEL) {
                spawner.setAmount(template.getCount() * ServerConfig.RATE_MOB_SPAWN);
            } else {
                spawner.setAmount(template.getCount());
            }

            spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
            spawner.setReflection(ReflectionManager.DEFAULT);
            spawner.setRespawnTime(0);

            if ((npcTemplate.isRaid || npcTemplate.getInstanceClass() == MonsterWithLongRespawnInstance.class) && group.equals(PeriodOfDay.NONE.name())) {
                RaidBossSpawnManager.getInstance().addNewSpawn(npcTemplate.getNpcId(), spawner);
            }
        }

        return spawnerList;
    }

    public void spawnAll() {
        spawn(PeriodOfDay.NONE.name());
    }

    public void spawn(final String group) {
        final List<Spawner> spawnerList = _spawns.get(group);
        if (spawnerList == null) {
            return;
        }

        int npcSpawnCount = 0;

        for (final Spawner spawner : spawnerList) {
            npcSpawnCount += spawner.init();
            if (npcSpawnCount % ServerConfig.MAX_SPAWN_NUM_PER_ONE_TICK == 0 && npcSpawnCount != 0) {
                _log.info("SpawnManager: spawned " + npcSpawnCount + " npc for group: " + group);
            }
        }
        if (group.equalsIgnoreCase(PeriodOfDay.NONE.name())) {
            _log.info("SpawnManager: spawned " + npcSpawnCount + " npc; spawns: " + spawnerList.size() + "; group: " + group);
        }
    }

    public void despawn(final String group) {
        final List<Spawner> spawnerList = _spawns.get(group);
        if (spawnerList == null) {
            return;
        }

        for (final Spawner spawner : spawnerList) {
            spawner.deleteAll();
        }
    }

    public List<Spawner> getSpawners(final String group) {
        final List<Spawner> list = _spawns.get(group);
        return list == null ? Collections.<Spawner>emptyList() : list;
    }

    public int getSpawnSize() {
        return _spawns.size();
    }

    public void reloadAll() {
        RaidBossSpawnManager.getInstance().cleanUp();
        for (final List<Spawner> spawnerList : _spawns.values()) {
            for (final Spawner spawner : spawnerList) {
                spawner.deleteAll();
            }
        }

        RaidBossSpawnManager.getInstance().reloadBosses();

        spawnAll();
        SevenSigns.getInstance().spawnSevenSignsNPC();

        if (GameTimeManager.getInstance().isNowNight()) {
            _listeners.onNight();
        } else {
            _listeners.onDay();
        }
    }

    private static class LazyHolder {
        private static final SpawnManager INSTANCE = new SpawnManager();
    }

    private class Listeners implements OnDayNightChangeListener {
        @Override
        public void onDay() {
            despawn(PeriodOfDay.NIGHT.name());
            spawn(PeriodOfDay.DAY.name());
        }

        @Override
        public void onNight() {
            despawn(PeriodOfDay.DAY.name());
            spawn(PeriodOfDay.NIGHT.name());
        }
    }
}