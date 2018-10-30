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

public class ValakasManager implements OnInitScriptListener, OnReloadScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(ValakasManager.class);
    private static final int Valakas = 29028;
    private static final int FWV_APPTIMEOFVALAKAS = BossConfig.ValakasUptime * 500;     // 15 mins
    public static CronExpression pattern = QuartzUtils.createCronExpression(CustomBossSpawnConfig.valakasCron);
    private static List<NpcInstance> _spawnedMinions = new ArrayList<NpcInstance>();
    private static BossInstance _valakas = null;
    // Tasks.
    private static ScheduledFuture<?> _valakasSpawnTask = null;
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static EpicBossState _state = null;
    private static Zone _zone = null;
    private static int FWV_FIXINTERVALOFVALAKAS = BossConfig.ValakasFixedRespawn * 60 * 60000;
    private static int FWV_RANDOMINTERVALOFVALAKAS = BossConfig.ValakasRandomRespawn * 60 * 60000;

    private static void banishForeigners() {
        for (Player player : getPlayersInside()) {
            player.teleToClosestTown();
        }
    }

    private static List<Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    private static long getRespawnInterval() {
        return (long) (AllSettingsConfig.ALT_RAID_RESPAWN_MULTIPLIER * (FWV_FIXINTERVALOFVALAKAS
                + Rnd.get(0, FWV_RANDOMINTERVALOFVALAKAS)));
    }

    public static Zone getZone() {
        return _zone;
    }

    public static void notifyDeath(BossInstance boss) {
        // If death notification is not from the default antharas, do nothing
        if (_valakas != boss) {
            return;
        }
        ThreadPoolManager.getInstance().schedule(new SpawnDespawn(12), 10);
    }

    private static void deathProcessing() {
        if (CustomBossSpawnConfig.activateCustomSpawn)
            _state.setWeaklyRespawnDate(pattern, CustomBossSpawnConfig.valakasRandomMinutes);
        else
            _state.setRespawnDate(getRespawnInterval());
        _state.setState(State.INTERVAL);
        _state.update();

        SpawnManager.getInstance().spawn("valakas_tp_cubic_spawn");
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                SpawnManager.getInstance().despawn("valakas_tp_cubic_spawn");
            }
        }, 20 * 60 * 1000L);
        Log.add("Valakas died", "org/mmocore/gameserver/scripts/bosses");
    }

    // Start interval.
    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState() == State.ALIVE) {
            _state.setState(State.NOTSPAWN);
            _state.update();
            return;
        }

        if (_state.getState() != State.INTERVAL) {
            if (CustomBossSpawnConfig.activateCustomSpawn)
                _state.setWeaklyRespawnDate(pattern, CustomBossSpawnConfig.valakasRandomMinutes);
            else
                _state.setRespawnDate(getRespawnInterval());
            _state.setState(State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    // Clean Valakas's lair.
    private static void setUnspawn() {
        // Eliminate players.
        banishForeigners();

        if (_valakas != null) {
            _valakas.deleteMe();
            _valakas = null;
        }

        for (NpcInstance npc : _spawnedMinions) {
            npc.deleteMe();
        }
        _spawnedMinions.clear();

        if (_valakasSpawnTask != null) {
            _valakasSpawnTask.cancel(false);
            _valakasSpawnTask = null;
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

    public synchronized static void setValakasSpawnTask() {
        if (_valakasSpawnTask == null) {
            _valakasSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(1), FWV_APPTIMEOFVALAKAS);
        }
    }

    public static void broadcastScreenMessage(NpcString npcs) {
        final GameServerPacket packet = new ExShowScreenMessage(npcs, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
        for (Player p : getPlayersInside())
            p.sendPacket(packet);
    }

    public static void addValakasMinion(NpcInstance npc) {
        _spawnedMinions.add(npc);
    }

    public static NestState checkNestEntranceCond(int tryCount) {
        if (_state.getState() == State.INTERVAL) {
            return NestState.NOT_AVAILABLE;
        }
        if (_state.getState() == State.ALIVE) {
            return NestState.ALREADY_ATTACKED;
        }
        if (getZone().getInsidePlayers().size() + tryCount > BossConfig.ValakasLimit) {
            return NestState.LIMIT_EXCEEDED;
        }
        return NestState.ALLOW;
    }

    public static void notifyEntrance() {
        setValakasSpawnTask();
    }

    public static EpicBossState getState() {
        return _state;
    }

    private void init() {
        _state = new EpicBossState(Valakas);
        _zone = ReflectionUtils.getZone("[valakas_epic]");
        _log.info("ValakasManager: State of Valakas is {}.", _state.getState());
        if (_state.getState() != State.NOTSPAWN) {
            setIntervalEndTask();
        }

        _log.info("ValakasManager: Next spawn date of Valakas is {}.", TimeUtils.dateTimeFormat(_state.getRespawnDate()));
    }

    @Override
    public void onInit() {
        init();
    }

    @Override
    public void onReload() {
        sleep();
    }

    private static class IntervalEnd extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            _state.setState(State.NOTSPAWN);
            _state.update();
        }
    }

    private static class SpawnDespawn extends RunnableImpl {
        private int _distance = 2550;
        private int _taskId;
        private List<Player> _players = getPlayersInside();

        SpawnDespawn(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() throws Exception {
            switch (_taskId) {
                case 1:
                    // Do spawn.
                    _valakas = (BossInstance) NpcUtils.spawnSingle(Valakas, new Location(212852, -114842, -1632, 833));
                    _valakas.block();
                    _valakas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS03_A", 1, _valakas.getObjectId(), _valakas.getLoc()));
                    if (CustomBossSpawnConfig.activateCustomSpawn)
                        _state.setWeaklyRespawnDate(pattern, CustomBossSpawnConfig.valakasRandomMinutes);
                    else
                        _state.setRespawnDate(Rnd.get(FWV_FIXINTERVALOFVALAKAS, FWV_FIXINTERVALOFVALAKAS));
                    _state.setState(State.ALIVE);
                    _state.update();
                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(2), 10);
                    break;
                case 2:
                    // Do social.
                    _valakas.broadcastPacket(new SocialAction(_valakas.getObjectId(), 1));

                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1800, 180, -1, 1500, 15000, 10000, 0, 0, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(3), 1500);
                    break;
                case 3:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1300, 180, -5, 3000, 15000, 10000, 0, -5, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(4), 3300);
                    break;
                case 4:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 500, 180, -8, 600, 15000, 10000, 0, 60, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(5), 2900);
                    break;
                case 5:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 800, 180, -8, 2700, 15000, 10000, 0, 30, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(6), 2700);
                    break;
                case 6:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 200, 250, 70, 0, 15000, 10000, 30, 80, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(7), 1);
                    break;
                case 7:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1100, 250, 70, 2500, 15000, 10000, 30, 80, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(8), 3200);
                    break;
                case 8:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 700, 150, 30, 0, 15000, 10000, -10, 60, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(9), 1400);
                    break;
                case 9:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1200, 150, 20, 2900, 15000, 10000, -10, 30, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(10), 6700);
                    break;
                case 10:
                    // Set camera.
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 750, 170, -10, 3400, 15000, 4000, 10, -15, 1, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(11), 5700);
                    break;
                case 11:
                    // Reset camera.
                    for (Player pc : _players) {
                        pc.leaveMovieMode();
                    }

                    _valakas.unblock();
                    broadcastScreenMessage(NpcString.VALAKAS_ARROGAANT_FOOL_YOU_DARE_TO_CHALLENGE_ME);
                    _valakas.moveToLocation(new Location(Rnd.get(211080, 214909), Rnd.get(-115841, -112822), -1662, 0), 0, false);
                    break;

                // Death Movie
                case 12:
                    _valakas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "B03_D", 1, _valakas.getObjectId(), _valakas.getLoc()));
                    deathProcessing();
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(13), 500);
                    break;
                case 13:
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1100, 210, -5, 3000, 15000, 10000, -13, 0, 1, 1, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(14), 3500);
                    break;
                case 14:
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1300, 200, -8, 3000, 15000, 10000, 0, 15, 1, 1, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(15), 4500);
                    break;
                case 15:
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1000, 190, 0, 500, 15000, 10000, 0, 10, 1, 1, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(16), 500);
                    break;
                case 16:
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1700, 120, 0, 2500, 15000, 10000, 12, 40, 1, 1, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(17), 4600);
                    break;
                case 17:
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1700, 20, 0, 700, 15000, 10000, 10, 10, 1, 1, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(18), 750);
                    break;
                case 18:
                    for (Player pc : _players) {
                        if (pc.getDistance(_valakas) <= _distance) {
                            pc.enterMovieMode();
                            pc.specialCamera3(_valakas, 1700, 10, 0, 1000, 15000, 10000, 20, 70, 1, 1, 0);
                        } else {
                            pc.leaveMovieMode();
                        }
                    }

                    _socialTask = ThreadPoolManager.getInstance().schedule(new SpawnDespawn(19), 2500);
                    break;
                case 19:
                    for (Player pc : _players) {
                        pc.leaveMovieMode();
                    }
                    final GameServerPacket essmPacket = new ExShowScreenMessage(NpcString.VALAKAS_THE_EVIL_FIRE_DRAGON_VALAKAS_DEFEATED, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true);
                    for (final Player player : GameObjectsStorage.getPlayers()) {
                        player.sendPacket(essmPacket);
                    }
                    if (GameObjectsStorage.getByNpcId(4326) == null) {
                        SpawnManager.getInstance().spawn("nevitt_herald_group");
                    }
                    break;
            }
        }
    }
}