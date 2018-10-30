package org.mmocore.gameserver.manager.naia;

import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author pchayka
 */
public final class NaiaCoreManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NaiaCoreManager.class);

    private static final Territory _coreTerritory = new Territory().add(new CustomPolygon(5)
            .add(-44789, 246305).add(-44130, 247452).add(-46092, 248606)
            .add(-46790, 247414).add(-46139, 246304)
            .setZmin(-14220).setZmax(-13800));
    //Spores
    private static final int fireSpore = 25605;
    private static final int waterSpore = 25606;
    private static final int windSpore = 25607;
    private static final int earthSpore = 25608;
    private static final List<Integer> spores = Arrays.asList(
            fireSpore,
            waterSpore,
            windSpore,
            earthSpore
    );
    //Bosses
    private static final int fireEpidos = 25609;
    private static final int waterEpidos = 25610;
    private static final int windEpidos = 25611;
    private static final int earthEpidos = 25612;
    private static final List<Integer> epidoses = Arrays.asList(
            fireEpidos,
            waterEpidos,
            windEpidos,
            earthEpidos
    );
    private static final int teleCube = 32376;
    private static final int respawnDelay = 120; // 2min
    private static final long coreClearTime = 4 * 60 * 60 * 1000L; // 4hours
    private static final Location spawnLoc = new Location(-45482, 246277, -14184);

    private static final ZoneListener _zoneListener = new ZoneListener();

    private static boolean _active;
    private static boolean _bossSpawned;

    private NaiaCoreManager() {
        final Zone _zone = ReflectionUtils.getZone("[naia_core_poison]");
        _zone.setActive(true);
        _zone.addListener(_zoneListener);

        LOGGER.info("Naia Core Manager: Loaded");
    }

    public static NaiaCoreManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void launchNaiaCore() {
        if (isActive()) {
            return;
        }

        _active = true;
        ReflectionUtils.getDoor(18250025).closeMe();
        spawnSpores();
        ThreadPoolManager.getInstance().schedule(new ClearCore(), coreClearTime);
    }

    private static boolean isActive() {
        return _active;
    }

    private static void spawnSpores() {
        spawnToRoom(fireSpore, 10, _coreTerritory);
        spawnToRoom(waterSpore, 10, _coreTerritory);
        spawnToRoom(windSpore, 10, _coreTerritory);
        spawnToRoom(earthSpore, 10, _coreTerritory);
    }

    public static void spawnEpidos(final int index) {
        if (!isActive()) {
            return;
        }
        int epidostospawn = 0;
        switch (index) {
            case 1: {
                epidostospawn = fireEpidos;
                break;
            }
            case 2: {
                epidostospawn = waterEpidos;
                break;
            }
            case 3: {
                epidostospawn = windEpidos;
                break;
            }
            case 4: {
                epidostospawn = earthEpidos;
                break;
            }
            default:
                break;
        }
        try {
            NpcUtils.spawnSingle(epidostospawn, spawnLoc);
            _bossSpawned = true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isBossSpawned() {
        return _bossSpawned;
    }

    public static void removeSporesAndSpawnCube() {
        GameObjectsStorage.getAllByNpcId(spores, false).forEach(GameObject::deleteMe);

        try {
            final NpcInstance cube = NpcUtils.spawnSingle(teleCube, spawnLoc);
            ChatUtils.shout(cube, NpcString.NONE, "Teleportation to Beleth Throne Room is available for 2 minutes");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void spawnToRoom(final int mobId, final int count, final Territory territory) {
        for (int i = 0; i < count; i++) {
            try {
                final SimpleSpawner sp = new SimpleSpawner(mobId);
                sp.setLoc(Territory.getRandomLoc(territory).setH(Rnd.get(65535)));
                sp.setRespawnDelay(respawnDelay, 30);
                sp.setAmount(1);
                sp.doSpawn(true);
                sp.startRespawn();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClearCore extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            GameObjectsStorage.getAllByNpcId(spores, false).forEach(NpcInstance::deleteMe);
            GameObjectsStorage.getAllByNpcId(epidoses, false).forEach(NpcInstance::deleteMe);

            _active = false;
            ReflectionUtils.getDoor(18250025).openMe();
        }
    }

    private static final class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(final Zone zone, final Creature actor) {
            if (!actor.isPlayable())
                return;

            final Player player = actor.getPlayer();
            if (!NaiaTowerManager.isValidPlayer(player) && !player.isGM())
                player.teleToClosestTown();
        }

        @Override
        public void onZoneLeave(final Zone zone, final Creature actor) {
        }
    }

    private static class LazyHolder {
        private static final NaiaCoreManager INSTANCE = new NaiaCoreManager();
    }
}