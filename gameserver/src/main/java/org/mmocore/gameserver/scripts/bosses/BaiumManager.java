package org.mmocore.gameserver.scripts.bosses;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.BossConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.CustomBossSpawnConfig;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.Earthquake;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.scripts.bosses.EpicBossState.State;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.*;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.mmocore.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

/**
 * @author Mangol
 * @author ?
 * @version Freya
 */
public class BaiumManager implements OnInitScriptListener, OnReloadScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(BaiumManager.class);
    private final static int baium = 29020;
    private final static int boss06_angel = 29021;
    private final static int cube = 31759;
    private final static int baium_stone = 29025;
    private final static int TIMER_IDLE_COMBAT = BossConfig.BaiumTimeUntilSleep * 1000;
    private static EpicBossState _state;
    private static long _lastAttackTime = 0;
    private static Zone _zone;
    private static boolean Dying = false;
    private static final int FWB_FIXINTERVALOFBAIUM = BossConfig.BaiumFixedRespawn * 60 * 60000;
    private static final int FWB_RANDOMINTERVALOFBAIUM = BossConfig.BaiumRandomRespawn * 60 * 60000;
    private static final CronExpression pattern = QuartzUtils.createCronExpression(CustomBossSpawnConfig.baiumCron);

    private synchronized static void checkAnnihilated() {
        if (isPlayersAnnihilated()) {
            ThreadPoolManager.getInstance().schedule(new Task(3), 5000);
        }
    }

    public static void onBaiumDie(Creature self) {
        if (Dying)
            return;
        Dying = true;
        NpcUtils.spawnSingle(cube, new Location(115017, 15549, 10090));
        self.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS01_D", 1, 0, self.getLoc()));
        if (CustomBossSpawnConfig.activateCustomSpawn)
            StateBaium(pattern, CustomBossSpawnConfig.baiumRandomMinutes, State.INTERVAL);
        else
            StateBaium(getRespawnInterval(), State.INTERVAL);
        Log.add("Baium died", "org/mmocore/gameserver/scripts/bosses");
        // Удаляем ангелов если они есть
        deleteNpcs(boss06_angel);
        ThreadPoolManager.getInstance().schedule(new Task(4), 900000);
    }

    private static void setIntervalEndTask() {
        // Телепортируем играков
        TeleportInMyTerritory();
        // Удаляем ангелов если они есть
        deleteNpcs(boss06_angel);
        // Удаляем баюма
        deleteNpcs(29020);
        // init state of Baium's lair.
        if (_state.getState() != State.INTERVAL) {
            if (CustomBossSpawnConfig.activateCustomSpawn)
                StateBaium(pattern, CustomBossSpawnConfig.baiumRandomMinutes, State.INTERVAL);
            else
                StateBaium(getRespawnInterval(), State.INTERVAL);
        }
        ThreadPoolManager.getInstance().schedule(new Task(1), _state.getInterval());
    }

    public static void setLastAttackTime() {
        _lastAttackTime = System.currentTimeMillis();
    }

    private static void sleepBaium() {
        // Телепортируем играков
        TeleportInMyTerritory();
        // Удаляем ангелов если они есть
        deleteNpcs(boss06_angel);
        // Удаляем баюма
        deleteNpcs(29020);
        Log.add("Baium going to sleep, spawning statue", "org/mmocore/gameserver/scripts/bosses");
        StateBaium();
        // Спавним статую
        NpcUtils.spawnSingle(baium_stone, new Location(116033, 17447, 10107, -36482));
    }

    public static void spawnBaium(NpcInstance NpcBaium, Player awake_by) {
        Dying = false;
        NpcInstance _npcBaium = NpcBaium;
        NpcInstance baiumSpawn = NpcUtils.spawnSingle(baium, new Location(116033, 17447, 10107, -36482));
        _npcBaium.deleteMe();
        final BossInstance baium = (BossInstance) baiumSpawn;
        if (CustomBossSpawnConfig.activateCustomSpawn)
            StateBaium(pattern, CustomBossSpawnConfig.baiumRandomMinutes, State.ALIVE);
        else
            StateBaium(getRespawnInterval(), State.ALIVE);
        Log.add("Spawned Baium, awake by: " + awake_by, "org/mmocore/gameserver/scripts/bosses");
        setLastAttackTime();
        ThreadPoolManager.getInstance().schedule(new StartBaiumTask(baium, awake_by, 1), 2000);
    }

    private static void deleteNpcs(int npc) {
        List<NpcInstance> insideNpc = getZone().getInsideNpcs();
        for (NpcInstance delete : insideNpc) {
            if (delete != null && delete.getNpcId() == npc) {
                delete.deleteMe();
            }
        }
    }

    private static void TeleportInMyTerritory() {
        for (Player player : getPlayersInside()) {
            if (player != null) {
                player.teleToLocation(new Location(120112, 18208, -5152, 900));
            }
        }
    }

    private static void StateBaium(final int respawn_data, final State stat) {
        _state.setRespawnDate(respawn_data);
        _state.setState(stat);
        _state.update();
    }

    private static void StateBaium() {
        _state.setState(State.NOTSPAWN);
        _state.update();
    }

    private static void StateBaium(CronExpression cron, int randomMinutes, State state) {
        _state.setWeaklyRespawnDate(cron, randomMinutes);
        _state.setState(state);
        _state.update();
    }

    private static List<Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    private static boolean isPlayersAnnihilated() {
        for (Player pc : getPlayersInside()) {
            if (!pc.isDead()) {
                return false;
            }
        }
        return true;
    }

    private static int getRespawnInterval() {
        return (int) (AllSettingsConfig.ALT_RAID_RESPAWN_MULTIPLIER * (FWB_FIXINTERVALOFBAIUM + Rnd.get(0, FWB_RANDOMINTERVALOFBAIUM)));
    }

    public static Zone getZone() {
        return _zone;
    }

    public static EpicBossState getState() {
        return _state;
    }

    private void init() {
        _state = new EpicBossState(baium);
        // Инитализируем зону
        _zone = ReflectionUtils.getZone("[baium_epic]");
        CharListenerList.addGlobal(new OnDeathListenerImpl());
        _log.info("BaiumManager: State of Baium is " + _state.getState() + '.');
        if (_state.getState() == State.NOTSPAWN) {
            // Спавним статую
            NpcUtils.spawnSingle(baium_stone, new Location(116033, 17447, 10107, -36482));
        } else if (_state.getState() == State.ALIVE) {
            StateBaium();
            // Спавним статую
            NpcUtils.spawnSingle(baium_stone, new Location(116033, 17447, 10107, -36482));
        } else if (_state.getState() == State.INTERVAL || _state.getState() == State.DEAD) {
            setIntervalEndTask();
        }
        _log.info("BaiumManager: Next spawn date: " + TimeUtils.dateTimeFormat(_state.getRespawnDate()));
    }

    @Override
    public void onInit() {
        init();
    }

    @Override
    public void onReload() {
        sleepBaium();
    }

    private static class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self)) {
                checkAnnihilated();
            } else if (self.isNpc() && self.getNpcId() == baium) {
                onBaiumDie(self);
            }
        }
    }

    public static class StartBaiumTask extends RunnableImpl {
        private final Player _player;
        private final BossInstance _baium;
        private final int _taskId;

        public StartBaiumTask(final BossInstance baium, final Player player, final int taskId) {
            _baium = baium;
            _player = player;
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    _player.sendPacket(new ExShowScreenMessage(NpcString.NOT_EVEN_THE_GODS_THEMSELVES_COULD_TOUCH_ME_BUT_YOU_S_YOU_DARE_CHALLENGE_ME_IGNORANT_MORTAL, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
                    _baium.startImmobilized();
                    _baium.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_A", 1, 0, _baium.getLoc()));
                    _baium.broadcastPacket(new SocialAction(_baium.getObjectId(), 2));
                    _baium.broadcastPacket(new Earthquake(_baium.getLoc(), 40, 10));
                    ThreadPoolManager.getInstance().schedule(new StartBaiumTask(_baium, _player, 2), 9500);
                    break;
                case 2:
                    _baium.broadcastPacket(new SocialAction(_baium.getObjectId(), 3));
                    ThreadPoolManager.getInstance().schedule(new StartBaiumTask(_baium, _player, 3), 7300);
                    break;
                case 3:
                    _baium.stopImmobilized();
                    _baium.broadcastPacket(new SocialAction(_baium.getObjectId(), 1));
                    _baium.moveToLocation(_player.getLoc(), 0, true);
                    SkillEntry skill = SkillTable.getInstance().getSkillEntry(4136, 1);
                    if (_player != null && skill != null) {
                        _baium.setTarget(_player);
                        _baium.doCast(skill, _player, false);
                        ChatUtils.say(_baium, NpcString.HOW_DARE_YOU_WAKE_ME_NOW_YOU_SHALL_DIE);
                    }
                    ThreadPoolManager.getInstance().schedule(new StartBaiumTask(_baium, _player, 4), 8000);
                    break;
                // Спавн Ангелов
                case 4:
                    NpcUtils.spawnSingle(boss06_angel, new Location(115792, 16608, 10136));
                    NpcUtils.spawnSingle(boss06_angel, new Location(115168, 17200, 10136));
                    NpcUtils.spawnSingle(boss06_angel, new Location(115780, 15564, 10136));
                    NpcUtils.spawnSingle(boss06_angel, new Location(114880, 16236, 10136));
                    NpcUtils.spawnSingle(boss06_angel, new Location(114239, 17168, 10136));
                    ThreadPoolManager.getInstance().schedule(new StartBaiumTask(_baium, _player, 5), 36000);
                    ThreadPoolManager.getInstance().schedule(new Task(2), 600000);
                    break;
                case 5:
                    Location pos = new Location(Rnd.get(112826, 116241), Rnd.get(15575, 16375), 10078, 0);
                    if (_baium.getAI().getIntention() == AI_INTENTION_ACTIVE)
                        _baium.moveToLocation(pos, 0, false);
                    break;
            }
        }
    }

    public static class Task extends RunnableImpl {
        private final int _taskId;

        public Task(int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    StateBaium();
                    // Спавним статую
                    NpcUtils.spawnSingle(baium_stone, new Location(116033, 17447, 10107, -36482));
                    break;
                case 2:
                    if (_state.getState() == State.ALIVE) {
                        if (_lastAttackTime + TIMER_IDLE_COMBAT < System.currentTimeMillis()) {
                            sleepBaium();
                        } else {
                            ThreadPoolManager.getInstance().schedule(new Task(2), 60000);
                        }
                    }
                    break;
                //Уходим и спавним статую
                case 3:
                    sleepBaium();
                    break;
                // Таск на удаление Телепорт куба
                case 4:
                    deleteNpcs(cube);
                    break;
            }
        }
    }
}