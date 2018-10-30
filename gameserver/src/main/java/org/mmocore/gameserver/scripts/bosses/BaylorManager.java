package org.mmocore.gameserver.scripts.bosses;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.listener.actor.OnAttackListener;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FlyToLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FlyToLocation.FlyType;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class BaylorManager implements OnInitScriptListener, OnReloadScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaylorManager.class);
    private static final int Baylor = 29099;
    private static final int CrystalPrisonGuard = 29100;
    private static final int Parme = 32271;
    private static final int Oracle = 32279;
    private static final int BaylorChest = 29116;
    private static final Location _crystalineLocation[] = {
            new Location(154404, 140596, -12711, 44732),
            new Location(153574, 140402, -12711, 44732),
            new Location(152105, 141230, -12711, 44732),
            new Location(151877, 142095, -12711, 44732),
            new Location(152109, 142920, -12711, 44732),
            new Location(152730, 143555, -12711, 44732),
            new Location(154439, 143538, -12711, 44732),
            new Location(155246, 142068, -12711, 44732)
    };
    private static final Location demonicLocation[] = { // offlike
            new Location(153565, 141290, -12736, 0),
            new Location(153570, 142853, -12736, 0),
            new Location(152793, 142080, -12736, 0),
            new Location(154359, 142077, -12736, 0)
    };
    private static final int[] doors = {24220009, 24220011, 24220012, 24220014, 24220015, 24220016, 24220017, 24220019};
    private static final int FWBA_ACTIVITYTIMEOFMOBS = 120 * 60000;
    private static final int FWBA_FIXINTERVALOFBAYLORSPAWN = 1440 * 60000;
    private static final int FWBA_RANDOMINTERVALOFBAYLORSPAWN = 1440 * 60000;
    private static final boolean FWBA_ENABLESINGLEPLAYER = false;
    private static final AttackListener attackListener = new AttackListener();
    // Instance of monsters
    private static NpcInstance[] _crystaline = new NpcInstance[8];
    private static NpcInstance _baylor;
    // Tasks
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _activityTimeEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static ScheduledFuture<?> _endSceneTask = null;
    // State of baylor's lair.
    private static boolean _isAlreadyEnteredOtherParty = false;
    private static EpicBossState _state;
    private static Zone _zone;
    private static boolean Dying = false;
    private static boolean playerEscape = false;
    private static int currentReflection;

    public static NpcInstance spawn(Location loc, int npcId) {
        NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
        NpcInstance npc = template.getNewInstance();
        npc.setSpawnedLoc(loc);
        npc.setHeading(loc.h);
        npc.setLoc(loc);
        npc.setReflection(currentReflection);
        npc.spawnMe();
        return npc;
    }

    // Whether it is permitted to enter the baylor's lair is confirmed.
    public static int canIntoBaylorLair(Player pc) {
        if (pc.isGM()) {
            return 0;
        }
        if (!FWBA_ENABLESINGLEPLAYER && !pc.isInParty()) {
            return 4;
        } else if (_isAlreadyEnteredOtherParty) {
            return 2;
        } else if (_state.getState() == EpicBossState.State.NOTSPAWN) {
            return 0;
        } else if (_state.getState() == EpicBossState.State.ALIVE || _state.getState() == EpicBossState.State.DEAD) {
            return 1;
        } else if (_state.getState() == EpicBossState.State.INTERVAL) {
            return 3;
        } else {
            return 0;
        }
    }

    private synchronized static void checkAnnihilated() {
        if (isPlayersAnnihilated()) {
            setIntervalEndTask();
        }
    }

    // Teleporting player to baylor's lair.
    public synchronized static void entryToBaylorLair(Player pc) {
        currentReflection = pc.getReflectionId();
        _zone.setReflection(pc.getReflection());

        ReflectionManager.getInstance().get(currentReflection).closeDoor(24220008);
        ThreadPoolManager.getInstance().schedule(new BaylorSpawn(CrystalPrisonGuard), 20000);
        ThreadPoolManager.getInstance().schedule(new BaylorSpawn(Baylor), 40000);

        if (pc.getParty() == null) {
            pc.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
            pc.block();
        } else {
            List<Player> members = new ArrayList<Player>(); // list of member of teleport candidate.
            for (Player mem : pc.getParty().getPartyMembers())
            // teleporting it within alive and the range of recognition of the leader of the party.
            {
                if (!mem.isDead() && mem.isInRange(pc, 1500)) {
                    members.add(mem);
                }
            }
            for (Player mem : members) {
                mem.teleToLocation(153569 + Rnd.get(-80, 80), 142075 + Rnd.get(-80, 80), -12732);
                mem.block();
            }
        }
        _isAlreadyEnteredOtherParty = true;
    }

    private static List<Player> getPlayersInside() {
        List<Player> result = new ArrayList<Player>();
        for (Player player : getZone().getInsidePlayers()) {
            result.add(player);
        }
        return result;
    }

    private static int getRespawnInterval() {
        return (int) (AllSettingsConfig.ALT_RAID_RESPAWN_MULTIPLIER * (FWBA_FIXINTERVALOFBAYLORSPAWN + Rnd.get(0, FWBA_RANDOMINTERVALOFBAYLORSPAWN)));
    }

    public static Zone getZone() {
        return _zone;
    }

    private static void init() {
        _state = new EpicBossState(Baylor);
        _zone = ReflectionUtils.getZone("[baylor_epic]");
        _zone.addListener(BaylorZoneListener.getInstance());

        _isAlreadyEnteredOtherParty = false;

        LOGGER.info("BaylorManager : State of Baylor is " + _state.getState() + '.', "org/mmocore/gameserver/scripts/bosses");
        if (_state.getState() != EpicBossState.State.NOTSPAWN) {
            setIntervalEndTask();
        }

        LOGGER.info("BaylorManager : Next spawn date of Baylor is " + TimeUtils.dateTimeFormat(_state.getRespawnDate()) + '.', "org/mmocore/gameserver/scripts/bosses");
        LOGGER.info("BaylorManager : Init BaylorManager.", "org/mmocore/gameserver/scripts/bosses");
    }

    private static boolean isPlayersAnnihilated() {
        for (Player pc : getPlayersInside()) {
            if (!pc.isDead()) {
                return false;
            }
        }
        return true;
    }

    private static void onBaylorDie(final Creature baylor) {
        if (Dying) {
            return;
        }

        Dying = true;
        _state.setRespawnDate(getRespawnInterval());
        _state.setState(EpicBossState.State.INTERVAL);
        _state.update();

        LOGGER.info("Baylor died", "org/mmocore/gameserver/scripts/bosses");

        spawn(baylor.getLoc(), BaylorChest);

        spawn(new Location(153570, 142067, -9727, 55500), Parme);
        spawn(new Location(153575, 142075, -12736, 55500), Oracle);

        startCollapse();
    }

    // Task of interval of baylor spawn.
    private static void setIntervalEndTask() {
        setUnspawn();

        if (_state.getState() == EpicBossState.State.ALIVE) {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
            return;
        }

        if (_state.getState() != EpicBossState.State.INTERVAL) {
            _state.setRespawnDate(getRespawnInterval());
            _state.setState(EpicBossState.State.INTERVAL);
            _state.update();
        }

        _intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
    }

    // Clean up Baylor's lair.
    private static void setUnspawn() {
        if (!_isAlreadyEnteredOtherParty) {
            return;
        }
        _isAlreadyEnteredOtherParty = false;

        startCollapse();

        if (_baylor != null) {
            _baylor.deleteMe();
        }
        _baylor = null;

        for (NpcInstance npc : _crystaline) {
            if (npc != null) {
                npc.deleteMe();
            }
        }

        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_activityTimeEndTask != null) {
            _activityTimeEndTask.cancel(false);
            _activityTimeEndTask = null;
        }
    }

    private static void startCollapse() {
        if (currentReflection > 0) {
            Reflection reflection = ReflectionManager.getInstance().get(currentReflection);
            if (reflection != null) {
                reflection.startCollapseTimer(300000);
            }
            currentReflection = 0;
        }
    }

    @Override
    public void onInit() {
        init();
    }

    @Override
    public void onReload() {
        setUnspawn();
    }

    private static class ActivityTimeEnd extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            setIntervalEndTask();
        }
    }

    private static class BaylorSpawn extends RunnableImpl {
        private int _npcId;
        private Location _pos = new Location(153569, 142075, -12711, 44732);

        public BaylorSpawn(int npcId) {
            _npcId = npcId;
        }

        @Override
        public void runImpl() throws Exception {
            switch (_npcId) {
                case CrystalPrisonGuard:

                    Reflection ref = ReflectionManager.getInstance().get(currentReflection);
                    for (int doorId : doors) {
                        ref.openDoor(doorId);
                    }

                    for (int i = 0; i < _crystalineLocation.length; i++) {
                        _crystaline[i] = spawn(_crystalineLocation[i], CrystalPrisonGuard);
                        _crystaline[i].setRunning();
                        _crystaline[i].moveToLocation(_pos, 300, false);
                        ThreadPoolManager.getInstance().schedule(new Social(_crystaline[i], 2), 15000);
                    }

                    break;
                case Baylor:
                    Dying = false;
                    playerEscape = false;

                    _baylor = spawn(new Location(153569, 142075, -12732, 59864), Baylor);
                    _baylor.addListener(BaylorDeathListener.getInstance());

                    _state.setRespawnDate(getRespawnInterval() + FWBA_ACTIVITYTIMEOFMOBS);
                    _state.setState(EpicBossState.State.ALIVE);
                    _state.update();

                    if (_socialTask != null) {
                        _socialTask.cancel(false);
                        _socialTask = null;
                    }
                    _socialTask = ThreadPoolManager.getInstance().schedule(new Social(_baylor, 1), 500);

                    if (_endSceneTask != null) {
                        _endSceneTask.cancel(false);
                        _endSceneTask = null;
                    }
                    _endSceneTask = ThreadPoolManager.getInstance().schedule(new EndScene(), 23000);

                    if (_activityTimeEndTask != null) {
                        _activityTimeEndTask.cancel(false);
                        _activityTimeEndTask = null;
                    }
                    _activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWBA_ACTIVITYTIMEOFMOBS);

                    break;
            }
        }
    }

    // Interval end.
    private static class IntervalEnd extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            _state.setState(EpicBossState.State.NOTSPAWN);
            _state.update();
        }
    }

    private static class Social extends RunnableImpl {
        private int _action;
        private NpcInstance _npc;

        public Social(NpcInstance npc, int actionId) {
            _npc = npc;
            _action = actionId;
        }

        @Override
        public void runImpl() throws Exception {
            _npc.broadcastPacket(new SocialAction(_npc.getObjectId(), _action));
        }
    }

    private static class EndScene extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            for (Player player : getPlayersInside()) {
                player.unblock();
                if (_baylor != null) {
                    double angle = PositionUtils.convertHeadingToDegree(_baylor.getHeading());
                    double radian = Math.toRadians(angle - 90);
                    int x1 = -(int) (Math.sin(radian) * 600);
                    int y1 = (int) (Math.cos(radian) * 600);
                    Location flyLoc = GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), player.getX() + x1, player.getY() + y1, player.getGeoIndex());
                    player.setLoc(flyLoc);
                    player.broadcastPacket(new FlyToLocation(player, flyLoc, FlyType.THROW_HORIZONTAL));
                }
            }
            for (int i = 0; i < _crystaline.length; i++) {
                NpcInstance npc = _crystaline[i];
                if (npc != null) {
                    npc.reduceCurrentHp(npc.getMaxHp() + 1, npc, null, true, true, false, false, false, false, false);
                }
            }
            for (int i = 0; i < demonicLocation.length; i++) {
                spawn(demonicLocation[i], CrystalPrisonGuard);
                for (final NpcInstance npcs : _baylor.getReflection().getNpcs()) {
                    if (npcs.getNpcId() == CrystalPrisonGuard) {
                        npcs.addListener(attackListener);
                    }
                }
            }
        }
    }

    private static class BaylorZoneListener implements OnZoneEnterLeaveListener {
        private static OnZoneEnterLeaveListener _instance = new BaylorZoneListener();

        public static OnZoneEnterLeaveListener getInstance() {
            return _instance;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature actor) {
            if (actor.isPlayer()) {
                actor.addListener(PlayerDeathListener.getInstance());
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature actor) {
            if (actor.isPlayer()) {
                actor.removeListener(PlayerDeathListener.getInstance());
            }
        }
    }

    private static class PlayerDeathListener implements OnDeathListener {
        private static OnDeathListener _instance = new PlayerDeathListener();

        public static OnDeathListener getInstance() {
            return _instance;
        }

        @Override
        public void onDeath(Creature actor, Creature killer) {
            checkAnnihilated();
        }
    }

    private static class BaylorDeathListener implements OnDeathListener {
        private static OnDeathListener _instance = new BaylorDeathListener();

        public static OnDeathListener getInstance() {
            return _instance;
        }

        @Override
        public void onDeath(Creature actor, Creature killer) {
            onBaylorDie(actor);
        }
    }

    private static class AttackListener implements OnAttackListener {
        @Override
        public void onAttack(final Creature actor, final Creature target) {
            if (!playerEscape && actor.isPlayer() && target.getNpcId() == CrystalPrisonGuard) {
                playerEscape = true;
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        final int i0 = Rnd.get(100);
                        if (i0 < 25) {
                            actor.teleToLocation(153565, 141290, -12736);
                        } else if (i0 < 50) {
                            actor.teleToLocation(153570, 142853, -12736);
                        } else if (i0 < 75) {
                            actor.teleToLocation(152793, 142080, -12736);
                        } else {
                            actor.teleToLocation(154359, 142077, -12736);
                        }
                        for (final NpcInstance npcs : actor.getReflection().getNpcs()) {
                            if (npcs.getNpcId() == CrystalPrisonGuard) {
                                npcs.deleteMe();
                            }
                        }
                    }
                }, 60000L);
            }
        }
    }
}