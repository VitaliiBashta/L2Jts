package org.mmocore.gameserver.scripts.bosses;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.BossConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.CustomBossSpawnConfig;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.EpicBossState.NestState;
import org.mmocore.gameserver.scripts.bosses.EpicBossState.State;
import org.mmocore.gameserver.utils.*;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author pchayka
 */

public class AntharasManager implements OnInitScriptListener, OnReloadScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(AntharasManager.class);

    // Constants
    private static final int _teleportCubeId = 31859;
    private static final int ANTHARAS_STRONG = 29068;

    private static final Location _teleportCubeLocation = new Location(177615, 114941, -7709, 0);
    private static final Location _antharasLocation = new Location(181911, 114835, -7678, 32542);
    public static final CronExpression pattern = QuartzUtils.createCronExpression(CustomBossSpawnConfig.antharasCron);
    // Models
    private static BossInstance _antharas = null;
    private static NpcInstance _teleCube = null;
    private static final List<NpcInstance> _spawnedMinions = new ArrayList<NpcInstance>();
    // tasks.
    private static ScheduledFuture<?> _monsterSpawnTask = null;
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    // Vars
    private static EpicBossState _state = null;
    private static Zone _zone = null;
    private static final int FWA_FIXINTERVALOFANTHARAS = BossConfig.AntharasFixedRespawn * 60 * 60000; // 11 days
    private static final int FWA_RANDOMINTERVALOFANTHARAS = BossConfig.AntharasRandomRespawn * 60 * 60000;
    private static final int FWA_APPTIMEOFANTHARAS = BossConfig.AntharasUptime * 500; // 15 mins

    private static void banishForeigners() {
        for (Player player : getPlayersInside()) {
            player.teleToClosestTown();
        }
    }

    private static List<Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    private static long getRespawnInterval() {
        return (long) (AllSettingsConfig.ALT_RAID_RESPAWN_MULTIPLIER * (FWA_FIXINTERVALOFANTHARAS
                + Rnd.get(0, FWA_RANDOMINTERVALOFANTHARAS)));
    }

    public static Zone getZone() {
        return _zone;
    }

    public static void notifyDeath(BossInstance boss) {
        // If death notification is not from the default antharas, do nothing
        if (_antharas != boss) {
            return;
        }
        ThreadPoolManager.getInstance().schedule(new AntharasSpawn(8), 10);
    }

    private static void deathProcessing() {
        if (CustomBossSpawnConfig.activateCustomSpawn)
            _state.setWeaklyRespawnDate(pattern, CustomBossSpawnConfig.antharasRandomMinutes);
        else
            _state.setRespawnDate(getRespawnInterval());
        _state.setState(State.INTERVAL);
        _state.update();

        _teleCube = NpcUtils.spawnSingle(_teleportCubeId, _teleportCubeLocation, 600000);
        Log.add("Antharas died", "org/mmocore/gameserver/scripts/bosses");
    }

    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState() == State.ALIVE) {
            _state.setState(State.NOTSPAWN);
            _state.update();
            return;
        }

        if (_state.getState() != State.INTERVAL) {
            if (CustomBossSpawnConfig.activateCustomSpawn)
                _state.setWeaklyRespawnDate(pattern, CustomBossSpawnConfig.antharasRandomMinutes);
            else
                _state.setRespawnDate(getRespawnInterval());
            _state.setState(State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    // clean Antharas's lair.
    private static void setUnspawn() {
        // eliminate players.
        banishForeigners();

        if (_antharas != null) {
            _antharas.deleteMe();
            _antharas = null;
        }

        for (NpcInstance npc : _spawnedMinions) {
            npc.deleteMe();
        }
        _spawnedMinions.clear();

        if (_teleCube != null) {
            _teleCube.deleteMe();
            _teleCube = null;
        }

        // not executed tasks is canceled.
        if (_monsterSpawnTask != null) {
            _monsterSpawnTask.cancel(false);
            _monsterSpawnTask = null;
        }
        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_socialTask != null) {
            _socialTask.cancel(false);
            _socialTask = null;
        }
    }

    public static void sleep() {
        setUnspawn();
        if (_state.getState() == State.ALIVE) {
            _state.setState(State.NOTSPAWN);
            _state.update();
        }
    }

    public synchronized static void setAntharasSpawnTask() {
        if (_monsterSpawnTask == null) {
            _monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(1), FWA_APPTIMEOFANTHARAS);
        }
    }

    public static void addSpawnedMinion(NpcInstance npc) {
        _spawnedMinions.add(npc);
    }

    public static NestState checkNestEntranceCond(int tryCount) {
        if (_state.getState() == State.INTERVAL) {
            return NestState.NOT_AVAILABLE;
        }
        if (_state.getState() == State.ALIVE) {
            return NestState.ALREADY_ATTACKED;
        }
        if (getZone().getInsidePlayers().size() + tryCount > BossConfig.AntharasLimit) {
            return NestState.LIMIT_EXCEEDED;
        }
        return NestState.ALLOW;
    }

    public static void notifyEntrance() {
        setAntharasSpawnTask();
    }

    public static EpicBossState getState() {
        return _state;
    }

    private void init() {
        _state = new EpicBossState(ANTHARAS_STRONG);
        _zone = ReflectionUtils.getZone("[antharas_epic]");
        _log.info("AntharasManager: State of Antharas is " + _state.getState() + '.');
        if (_state.getState() != State.NOTSPAWN) {
            setIntervalEndTask();
        }
        _log.info("AntharasManager: Next spawn date of Antharas is " + TimeUtils.dateTimeFormat(_state.getRespawnDate()) + '.');
    }

    @Override
    public void onInit() {
        init();
    }

    @Override
    public void onReload() {
        sleep();
    }

    private static class AntharasSpawn extends RunnableImpl {
        private final int _distance = 2550;
        private int _taskId = 0;
        private final List<Player> _players = getPlayersInside();

        AntharasSpawn(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    _antharas = (BossInstance) NpcUtils.spawnSingle(ANTHARAS_STRONG, _antharasLocation);
                    _antharas.setAggroRange(0);
                    if (CustomBossSpawnConfig.activateCustomSpawn)
                        _state.setWeaklyRespawnDate(pattern, CustomBossSpawnConfig.antharasRandomMinutes);
                    else
                        _state.setRespawnDate(Rnd.get(FWA_FIXINTERVALOFANTHARAS, FWA_FIXINTERVALOFANTHARAS));
                    _state.setState(State.ALIVE);
                    _state.update();
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(2), 2000);
                    break;
                case 2:
                    // set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_antharas, 700, 13, -19, 0, 10000, 20000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(3), 3000);
                    break;
                case 3:
                    // do social.
                    _antharas.broadcastPacket(new SocialAction(_antharas.getObjectId(), 1));

                    // set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_antharas, 700, 13, 0, 6000, 10000, 20000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(4), 10000);
                    break;
                case 4:
                    _antharas.broadcastPacket(new SocialAction(_antharas.getObjectId(), 2));
                    // set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_antharas, 3700, 0, -3, 0, 10000, 10000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(5), 200);
                    break;
                case 5:
                    // set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_antharas, 1100, 0, -3, 22000, 10000, 30000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(6), 10800);
                    break;
                case 6:
                    // set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_antharas, 1100, 0, -3, 300, 10000, 7000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(7), 7000);
                    break;
                case 7:
                    // reset camera.
                    for (Player pc : _players) {
                        pc.leaveMovieMode();
                    }

                    _antharas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, _antharas.getObjectId(), _antharas.getLoc()));
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.sendPacket(new ExShowScreenMessage(NpcString.S1_ANTHARAS_YOU_CANNOT_HOPE_TO_DEFEAT_ME, 4000, ScreenMessageAlign.TOP_CENTER, pc.getName()));
                        }
                    }
                    _antharas.setAggroRange(_antharas.getTemplate().aggroRange);
                    _antharas.setRunning();
                    _antharas.moveToLocation(new Location(179011, 114871, -7704), 0, false);
                    _antharas.setSpawnedLoc(new Location(179011, 114871, -7704));
                    break;
                case 8:
                    for (Player pc : _players) {
                        if (pc.getDistance(_antharas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_antharas, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }
                    _antharas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS01_D", 1, _antharas.getObjectId(), _antharas.getLoc()));
                    _socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(9), 13000);
                    break;
                case 9:
                    for (Player pc : _players) {
                        pc.leaveMovieMode();
                    }

                    final GameServerPacket essmPacket = new ExShowScreenMessage(NpcString.ANTHARAS_THE_EVIL_LAND_DRAGON_ANTHARAS_DEFEATED, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true);
                    for (final Player player : GameObjectsStorage.getPlayers()) {
                        player.sendPacket(essmPacket);
                    }
                    if (GameObjectsStorage.getByNpcId(4326) == null) {
                        SpawnManager.getInstance().spawn("nevitt_herald_group");
                    }
                    deathProcessing();
                    break;
            }
        }
    }

    // at end of interval.
    private static class IntervalEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            _state.setState(State.NOTSPAWN);
            _state.update();
        }
    }
}