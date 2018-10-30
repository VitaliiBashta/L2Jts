package org.mmocore.gameserver.scripts.bosses;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.scripts.bosses.EpicBossState.State;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Mangol
 * @version Freya
 */
public class SailrenManager implements OnInitScriptListener, OnReloadScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(SailrenManager.class);
    private final static int velociraptor_n = 22218;
    private final static int rhamphorhynchus = 22199;
    private final static int tyrannosaurus = 22215;
    private final static int cube = 31759;
    private static final int respawn_sailren = 1 * 3600000;//Час респавна
    private static EpicBossState _state;
    private static int velo_death_count = 0;
    private static int _party_enter;
    private static NpcInstance _velociraptor_n_1;
    private static NpcInstance _velociraptor_n_2;
    private static NpcInstance _velociraptor_n_3;
    private static NpcInstance _rhamphorhynchus_spawn;
    private static NpcInstance _tyrannosaurus_spawn;
    private static NpcInstance _sailren;
    private static NpcInstance _camera;
    private static Zone _zone;
    private static ScheduledFuture<?> _end_task = null;
    private final int sailren = 29065;

    private static List<Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    public static Zone getZone() {
        return _zone;
    }

    private synchronized static void checkAnnihilated() {
        if (isPlayersAnnihilated()) {
            ThreadPoolManager.getInstance().schedule(new Task(1), 5000);
        }
    }

    private static boolean isPlayersAnnihilated() {
        for (Player pc : getPlayersInside()) {
            if (!pc.isDead()) {
                return false;
            }
        }
        return true;
    }

    private static void onSailrenDie(Creature killer) {
        StateSailren(respawn_sailren, State.INTERVAL);
        Log.add("Sailren died", "org/mmocore/gameserver/scripts/bosses");
        _party_enter = 0;
        if (_end_task != null) {
            _end_task.cancel(false);
        }
        _end_task = null;
        _end_task = ThreadPoolManager.getInstance().schedule(new Task(1), 300000);
        ThreadPoolManager.getInstance().schedule(new Task(2), 8000);
    }

    // Почистим зону от всяких ишакоф!
    private static void clearn() {
        _party_enter = 0;
        if (_end_task != null) {
            _end_task.cancel(false);
            _end_task = null;
        }
        for (NpcInstance npc : getZone().getInsideNpcs()) {
            if (npc != null) {
                npc.deleteMe();
            }
        }
        for (Player player : getZone().getInsidePlayers()) {
            if (player != null) {
                player.teleToLocation(23575, -7727, -1272);
            }
        }
    }

    public static boolean isEnterToLair() {
        if (_state.getRespawnDate().getEpochSecond() < Instant.now().getEpochSecond()) {
            return true;
        }
        return false;
    }

    public static void setPartyEnter() {
        _party_enter = 1;
    }

    public static int getPartyEnter() {
        return _party_enter;
    }

    private static void StateSailren(final int respawn_data, final State stat) {
        _state.setRespawnDate(respawn_data);
        _state.setState(stat);
        _state.update();
    }

    public static void StateSailren(final State stat) {
        _state.setState(stat);
        _state.update();
    }

    private void init() {
        CharListenerList.addGlobal(new OnDeathListenerImpl());
        _zone = ReflectionUtils.getZone("[sailren_epic]");
        _state = new EpicBossState(sailren);
        clearn();
        _log.info("Sailren Manager: Loaded successfuly");
    }

    @Override
    public void onInit() {
        init();
    }

    @Override
    public void onReload() {
        clearn();
    }

    private static class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self.isPlayer() && _zone != null && _zone.checkIfInZone(self.getX(), self.getY())) {
                checkAnnihilated();
            }
            if (self == _velociraptor_n_1 || self == _velociraptor_n_2 || self == _velociraptor_n_3) {
                if (velo_death_count != 3) {
                    velo_death_count++;
                    if (velo_death_count == 3) {
                        ThreadPoolManager.getInstance().schedule(new StartAttack(2), 60000);
                        velo_death_count = 0;
                    }
                }
            } else if (self == _rhamphorhynchus_spawn) {
                ThreadPoolManager.getInstance().schedule(new StartAttack(3), 60000);
            } else if (self == _tyrannosaurus_spawn) {
                ThreadPoolManager.getInstance().schedule(new StartAttack(4), 60000);
            } else if (self == _sailren) {
                onSailrenDie(killer);
            }
        }
    }

    public static class Task extends RunnableImpl {
        private int _taskId;

        public Task(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    clearn();
                    break;
                case 2:
                    NpcUtils.spawnSingle(cube, new Location(27644, -6638, -2008));
                    break;
            }
        }
    }

    public static class StartAttack extends RunnableImpl {
        private int _taskId;

        public StartAttack(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    if (_end_task != null) {
                        _end_task.cancel(false);
                    }
                    _end_task = null;
                    _end_task = ThreadPoolManager.getInstance().schedule(new Task(2), 1000 * 3200);
                    _velociraptor_n_1 = NpcUtils.spawnSingle(velociraptor_n, new Location(27744, -6638, -2008));
                    _velociraptor_n_1.broadcastPacket(new SocialAction(_velociraptor_n_1.getObjectId(), 2));
                    _velociraptor_n_2 = NpcUtils.spawnSingle(velociraptor_n, new Location(27644, -6638, -2008));
                    _velociraptor_n_2.broadcastPacket(new SocialAction(_velociraptor_n_2.getObjectId(), 2));
                    _velociraptor_n_3 = NpcUtils.spawnSingle(velociraptor_n, new Location(27544, -6638, -2008));
                    _velociraptor_n_3.broadcastPacket(new SocialAction(_velociraptor_n_3.getObjectId(), 2));
                    break;
                case 2:
                    _rhamphorhynchus_spawn = NpcUtils.spawnSingle(rhamphorhynchus, new Location(27644, -6638, -2008));
                    _rhamphorhynchus_spawn.broadcastPacket(new SocialAction(_rhamphorhynchus_spawn.getObjectId(), 2));
                    break;
                case 3:
                    _tyrannosaurus_spawn = NpcUtils.spawnSingle(tyrannosaurus, new Location(27644, -6638, -2008));
                    _tyrannosaurus_spawn.broadcastPacket(new SocialAction(_tyrannosaurus_spawn.getObjectId(), 2));
                    break;
                case 4:
                    _camera = NpcUtils.spawnSingle(32110, new Location(27549, -6640, -2009));
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_camera, 2000), 4100);
                    ThreadPoolManager.getInstance().schedule(new StartAttack(5), 6500);
                    break;
                case 5:
                    _sailren = NpcUtils.spawnSingle(29065, new Location(27549, -6638, -2008));
                    _sailren.broadcastPacket(new SocialAction(_sailren.getObjectId(), 2));
                    break;
            }
        }
    }

    public static class StartMovie extends RunnableImpl {
        private int _taskId;
        private NpcInstance _npc;

        public StartMovie(NpcInstance npc, int taskId) {
            _npc = npc;
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 2000:
                    _npc.broadcastPacket(new MagicSkillUse(_npc, _npc, 5090, 1, 1000, 0));
                    for (Player player : getPlayersInside()) {
                        player.enterMovieMode();
                        player.specialCamera(_npc, 100, 180, 30, 1500, 3000, 20000, 0, 50, 1, 0);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_npc, 2001), 2800);
                    break;
                case 2001:
                    _npc.broadcastPacket(new MagicSkillUse(_npc, _npc, 5090, 1, 1000, 0));
                    for (Player player : getPlayersInside()) {
                        player.enterMovieMode();
                        player.specialCamera(_npc, 150, 270, 25, 1500, 3000, 20000, 0, 30, 1, 0);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_npc, 2002), 2800);
                    break;
                case 2002:
                    _npc.broadcastPacket(new MagicSkillUse(_npc, _npc, 5090, 1, 1000, 0));
                    for (Player player : getPlayersInside()) {
                        player.enterMovieMode();
                        player.specialCamera(_npc, 160, 360, 20, 1500, 3000, 20000, 10, 15, 1, 0);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_npc, 2003), 2800);
                    break;
                case 2003:
                    _npc.broadcastPacket(new MagicSkillUse(_npc, _npc, 5090, 1, 1000, 0));
                    for (Player player : getPlayersInside()) {
                        player.enterMovieMode();
                        player.specialCamera(_npc, 160, 450, 10, 1500, 3000, 20000, 0, 10, 1, 0);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_npc, 2004), 2800);
                    break;
                case 2004:
                    _npc.broadcastPacket(new MagicSkillUse(_npc, _npc, 5090, 1, 1000, 0));
                    for (Player player : getPlayersInside()) {
                        player.enterMovieMode();
                        player.specialCamera(_npc, 160, 560, 0, 1500, 7000, 20000, 0, 10, 1, 0);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_npc, 2005), 6800);
                    break;
                case 2005:
                    _npc.broadcastPacket(new MagicSkillUse(_npc, _npc, 5091, 1, 1000, 0));
                    for (Player player : getPlayersInside()) {
                        player.enterMovieMode();
                        player.specialCamera(_npc, 70, 560, 0, 1500, 1500, 7000, -15, 20, 1, 0);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartMovie(_npc, 2006), 1500);
                    break;
                case 2006:
                    for (Player player : getPlayersInside()) {
                        player.leaveMovieMode();
                    }
                    _npc.deleteMe();
                    break;
            }
        }
    }
}