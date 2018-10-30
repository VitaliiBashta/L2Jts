package org.mmocore.gameserver.scripts.bosses;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.clientCustoms.CustomBossSpawnConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.naia.NaiaTowerManager;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.Util;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author pchayka
 */
public class BelethManager implements OnInitScriptListener, OnReloadScriptListener {
    private static final Logger _log = LoggerFactory.getLogger(BelethManager.class);
    private static final long _closeDoorTimeDuration = 600000L; // 10min
    private static final int _clonesRespawnTimeTimeDuration = 40000; // 40sec
    private static final int _ringAvailableTime = 300000; // 5min
    private static final int _clearEntityTime = 600000; // 10min
    private static final long[] _belethRespawnTime = {8 * 24 * 60 * 60 * 1000, 10 * 60 * 60 * 1000}; // 8 days +- 10 hours
    private static final long _entityInactivityTime = 2 * 60 * 60 * 1000; // 2 hours
    private static final int _ringSpawnTime = 300000; // 5min
    private static final int _lastSpawnTime = 600000; // 10min
    private static final int DOOR = 20240001; // Throne door
    private static final int CORRDOOR = 20240002; // Corridor door
    private static final int COFFDOOR = 20240003; // Tomb door
    private static final int VORTEX = 29125; // Vortex.
    private static final int ELF = 29128; // Elf corpse.
    private static final int COFFIN = 32470; // Beleth's coffin.
    private static final int BELETH = 29118; // Beleth.
    private static final int CLONE = 29119; // Beleth's clone.
    private static final int locZ = -9353; // Z value for all of npcs
    private static final int[] VORTEXSPAWN = {
            16325,
            214983,
            -9353
    };
    private static final int[] COFFSPAWN = {
            12471,
            215602,
            -9360,
            49152
    };
    private static final int[] BELSPAWN = {
            16325,
            214614,
            -9353,
            49152
    };
    private static final int centerX = 16325; // Center of the room
    private static final int centerY = 213135;
    private static final Zone _zone = ReflectionUtils.getZone("[Beleth_room]");
    private static final ZoneListener _zoneListener = new ZoneListener();
    private static final List<NpcInstance> _npcList = new ArrayList<NpcInstance>();
    private static final CronExpression pattern = QuartzUtils.createCronExpression(CustomBossSpawnConfig.belethCron);
    private static boolean _entryLocked = false;
    private static boolean _ringAvailable = false;
    private static boolean _belethAlive = false;
    private static RaidBossInstance _beleth = null;
    private static final Map<MonsterInstance, Location> _clones = new ConcurrentHashMap<MonsterInstance, Location>();
    private static final Location[] _cloneLoc = new Location[56];
    private static ScheduledFuture<?> cloneRespawnTask;
    private static ScheduledFuture<?> ringSpawnTask;
    private static ScheduledFuture<?> lastSpawnTask;

    private static CommandChannel _cc;

    private static NpcInstance spawn(int npcId, int x, int y, int z, int h) {
        Location loc = new Location(x, y, z);
        NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
        NpcInstance npc = template.getNewInstance();
        npc.setSpawnedLoc(loc);
        npc.setLoc(loc);
        npc.setHeading(h);
        npc.spawnMe();
        return npc;
    }

    public static Zone getZone() {
        return _zone;
    }

    private static boolean checkPlayer(Player player) {
        if (player.isDead())
            return false;

        if (player.getLevel() < 80)
            return false;

        if (!NaiaTowerManager.getInstance().isValidPlayer(player))
            return false;

        return true;
    }

    public static boolean checkBossSpawnCond() {
        return ServerVariables.getLong("BelethKillTime", 0) <= System.currentTimeMillis();
    }

    public static void startSpawn() {
        ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.beleth_spawn), _closeDoorTimeDuration);
        ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.inactivity_check), _entityInactivityTime);
        ReflectionUtils.getDoor(DOOR).openMe();
        initSpawnLocs();
    }

    public static boolean isRingAvailable() {
        return _ringAvailable;
    }

    public static void setRingAvailable(boolean value) {
        _ringAvailable = value;
    }

    public static void setBelethDead() {
        if (_entryLocked && _belethAlive)
            ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.beleth_dead), 10);
    }

    private static void spawnClone(int id) {
        MonsterInstance clone;
        clone = (MonsterInstance) spawn(CLONE, _cloneLoc[id].x, _cloneLoc[id].y, locZ, 49152); // _cloneLoc[i].h
        _clones.put(clone, clone.getLoc());
    }

    private static void initSpawnLocs() {
        // Variables for Calculations
        double angle = Math.toRadians(22.5);
        int radius = 700;

        // Inner clone circle
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0)
                radius -= 50;
            else
                radius += 50;
            _cloneLoc[i] = new Location(centerX + (int) (radius * Math.sin(i * angle)), centerY + (int) (radius * Math.cos(i * angle)), PositionUtils.convertDegreeToClientHeading(270 - i * 22.5));
        }
        // Outer clone square
        radius = 1340;
        int mulX, mulY, addH = 3;
        double decX, decY;
        for (int i = 0; i < 16; i++) {
            if (i % 8 == 0)
                mulX = 0;
            else if (i < 8)
                mulX = -1;
            else
                mulX = 1;
            if (i == 4 || i == 12)
                mulY = 0;
            else if (i > 4 && i < 12)
                mulY = -1;
            else
                mulY = 1;
            if (i % 8 == 1 || i == 7 || i == 15)
                decX = 0.5;
            else
                decX = 1.0;
            if (i % 10 == 3 || i == 5 || i == 11)
                decY = 0.5;
            else
                decY = 1.0;
            if ((i + 2) % 4 == 0)
                addH++;
            _cloneLoc[i + 16] = new Location(centerX + (int) (radius * decX * mulX), centerY + (int) (radius * decY * mulY), PositionUtils.convertDegreeToClientHeading(180 + addH * 90));
        }
        // Octagon #2 - Another ring of clones like the inner square, that
        // spawns after some time
        angle = Math.toRadians(22.5);
        radius = 1000;
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0)
                radius -= 70;
            else
                radius += 70;
            _cloneLoc[i + 32] = new Location(centerX + (int) (radius * Math.sin(i * angle)), centerY + (int) (radius * Math.cos(i * angle)), _cloneLoc[i].h);
        }
        // Extra clones - Another 8 clones that spawn when beleth is close to
        // dying
        int order = 48;
        radius = 650;
        for (int i = 1; i < 16; i += 2) {
            if (i == 1 || i == 15)
                _cloneLoc[order] = new Location(_cloneLoc[i].x, _cloneLoc[i].y + radius, _cloneLoc[i + 16].h);
            else if (i == 3 || i == 5)
                _cloneLoc[order] = new Location(_cloneLoc[i].x + radius, _cloneLoc[i].y, _cloneLoc[i].h);
            else if (i == 7 || i == 9)
                _cloneLoc[order] = new Location(_cloneLoc[i].x, _cloneLoc[i].y - radius, _cloneLoc[i + 16].h);
            else if (i == 11 || i == 13)
                _cloneLoc[order] = new Location(_cloneLoc[i].x - radius, _cloneLoc[i].y, _cloneLoc[i].h);
            order++;
        }
    }

    public static CommandChannel getCC() {
        return _cc;
    }

    public static void setCC(CommandChannel cc) {
        _cc = cc;
    }

    public static boolean isBelethAlive() {
        return _belethAlive;
    }

    @Override
    public void onInit() {
        getZone().addListener(_zoneListener);
        _log.info("Beleth Manager: Loaded successfuly");
    }

    @Override
    public void onReload() {
        getZone().removeListener(_zoneListener);
    }

    private enum Event {
        none,
        beleth_spawn,
        beleth_despawn,
        clone_despawn,
        clone_spawn,
        ring_unset,
        beleth_dead,
        entity_clear,
        inactivity_check,
        spawn_ring,
        spawn_extras
    }

    public static class ZoneListener implements OnZoneEnterLeaveListener {

        @Override
        public void onZoneEnter(Zone zone, Creature actor) {
            if (!actor.isPlayer())
                return;

            Player player = actor.getPlayer();

            if (player.isGM() && player.isDebug())
                return;

            if (!checkPlayer(player) || !player.isInParty() || !player.getParty().isInCommandChannel() || player.getParty().getCommandChannel() != _cc)
                player.teleToClosestTown();
        }

        @Override
        public void onZoneLeave(Zone zone, Creature actor) {
        }
    }

    private static class CloneRespawnTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_clones == null || _clones.isEmpty())
                return;

            MonsterInstance nextclone;
            for (MonsterInstance clone : _clones.keySet())
                if (clone.isDead() || clone.isDeleted()) {
                    nextclone = (MonsterInstance) spawn(CLONE, _clones.get(clone).x, _clones.get(clone).y, locZ, 49152); // _cloneLoc[i].h
                    _clones.put(nextclone, nextclone.getLoc());
                    _clones.remove(clone);
                }
        }
    }

    public static class eventExecutor extends RunnableImpl {
        final Event _event;

        eventExecutor(Event event) {
            _event = event;
        }

        @Override
        public void runImpl() {
            switch (_event) {
                case beleth_spawn:
                    ReflectionUtils.getDoor(DOOR).closeMe();
                    _entryLocked = true;
                    NpcInstance temp = spawn(VORTEX, VORTEXSPAWN[0], VORTEXSPAWN[1], VORTEXSPAWN[2], 16384);
                    _npcList.add(temp);
                    _beleth = (RaidBossInstance) spawn(BELETH, BELSPAWN[0], BELSPAWN[1], BELSPAWN[2], BELSPAWN[3]);
                    _beleth.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS07_A", 1, _beleth.getObjectId(), _beleth.getLoc()));
                    _beleth.startImmobilized();
                    _belethAlive = true;
                    ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.clone_spawn), 10); // initial clones
                    ringSpawnTask = ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.spawn_ring), _ringSpawnTime); // inner ring
                    lastSpawnTask = ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.spawn_extras), _lastSpawnTime); // last clones
                    break;
                case clone_spawn:
                    MonsterInstance clone;
                    for (int i = 0; i < 32; i++) {
                        clone = (MonsterInstance) spawn(CLONE, _cloneLoc[i].x, _cloneLoc[i].y, locZ, 49152); // _cloneLoc[i].h
                        _clones.put(clone, clone.getLoc());
                    }
                    cloneRespawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CloneRespawnTask(), _clonesRespawnTimeTimeDuration, _clonesRespawnTimeTimeDuration);
                    break;
                case spawn_ring:
                    for (int i = 32; i < 48; i++)
                        spawnClone(i);
                    break;
                case spawn_extras:
                    for (int i = 48; i < 56; i++)
                        spawnClone(i);
                    break;
                case beleth_dead:
                    if (cloneRespawnTask != null) {
                        cloneRespawnTask.cancel(false);
                        cloneRespawnTask = null;
                    }
                    if (ringSpawnTask != null) {
                        ringSpawnTask.cancel(false);
                        ringSpawnTask = null;
                    }
                    if (lastSpawnTask != null) {
                        lastSpawnTask.cancel(false);
                        lastSpawnTask = null;
                    }
                    temp = spawn(ELF, _beleth.getLoc().x, _beleth.getLoc().y, locZ, BELSPAWN[3]);
                    _npcList.add(temp);
                    temp = spawn(COFFIN, COFFSPAWN[0], COFFSPAWN[1], COFFSPAWN[2], COFFSPAWN[3]);
                    _npcList.add(temp);
                    ReflectionUtils.getDoor(CORRDOOR).openMe();
                    ReflectionUtils.getDoor(COFFDOOR).openMe();
                    setRingAvailable(true);
                    _belethAlive = false;
                    if (CustomBossSpawnConfig.activateCustomSpawn)
                        ServerVariables.set("BelethKillTime", Util.getCronMillis(pattern)
                                + Rnd.get(0, CustomBossSpawnConfig.belethRandomMinutes * 60000));
                    else
                        ServerVariables.set("BelethKillTime", System.currentTimeMillis() + _belethRespawnTime[0]
                                + Rnd.get(-_belethRespawnTime[1], _belethRespawnTime[1]));
                    _log.info("Killed RaidBoss Beleth.");
                    for (Player i : _zone.getInsidePlayers())
                        i.sendMessage("Beleth's Lair will push you out in 10 minutes");
                    ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.clone_despawn), 10);
                    ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.ring_unset), _ringAvailableTime);
                    ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.entity_clear), _clearEntityTime);
                    break;
                case ring_unset:
                    setRingAvailable(false);
                    break;
                case entity_clear:
                    for (NpcInstance n : _npcList)
                        if (n != null)
                            n.deleteMe();
                    _npcList.clear();

                    // Close coffin and corridor doors
                    ReflectionUtils.getDoor(CORRDOOR).closeMe();
                    ReflectionUtils.getDoor(COFFDOOR).closeMe();

                    //oust players
                    for (Player i : _zone.getInsidePlayers()) {
                        i.teleToLocation(new Location(-11802, 236360, -3271));
                        i.sendMessage("Beleth's Lair has become unstable so you've been teleported out");
                    }
                    _entryLocked = false;
                    break;
                case clone_despawn:
                    for (MonsterInstance clonetodelete : _clones.keySet())
                        clonetodelete.deleteMe();
                    _clones.clear();
                    break;
                case inactivity_check:
                    if (!_beleth.isDead()) {
                        _beleth.deleteMe();
                        ThreadPoolManager.getInstance().schedule(new eventExecutor(Event.entity_clear), 10);
                    }
                    break;
            }
        }
    }
}