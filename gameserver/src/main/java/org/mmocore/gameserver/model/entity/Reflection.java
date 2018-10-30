package org.mmocore.gameserver.model.entity;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.listener.Listener;
import org.mmocore.commons.listener.ListenerList;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterInstancesDAO;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.listener.actor.door.impl.MasterOnOpenCloseListenerImpl;
import org.mmocore.gameserver.listener.reflection.OnReflectionCollapseListener;
import org.mmocore.gameserver.listener.zone.impl.*;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.HardSpawner;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.DoorTemplate;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.World;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Reflection {
    private static final AtomicInteger _nextId = new AtomicInteger();
    protected final List<Spawner> _spawns = new ArrayList<>();
    protected final List<GameObject> _objects = new ArrayList<>();
    protected final TIntHashSet _visitors = new TIntHashSet();
    protected final Lock lock = new ReentrantLock();
    private final int _id;
    private final ReflectionListenerList listeners = new ReflectionListenerList();
    // vars
    protected Map<Integer, DoorInstance> _doors = Collections.emptyMap();
    protected Map<String, Zone> _zones = Collections.emptyMap();
    protected Map<String, List<Spawner>> _spawners = Collections.emptyMap();
    protected int _playerCount;
    protected Party _party;
    protected CommandChannel _commandChannel;
    private String _name = StringUtils.EMPTY;
    private InstantZone _instance;
    private int _geoIndex;
    private Location _resetLoc; // место, к которому кидает при использовании SoE/unstuck, иначе выбрасывает в основной мир
    private Location _returnLoc; // если не прописано reset, но прописан return, то телепортит туда, одновременно перемещая в основной мир
    private Location _teleportLoc; // точка входа
    private int _collapseIfEmptyTime;

    private boolean _isCollapseStarted;
    private Future<?> _collapseTask;
    private Future<?> _collapse1minTask;
    private Future<?> _hiddencollapseTask;

    public Reflection() {
        this(_nextId.incrementAndGet());
    }

    protected Reflection(final int id) {
        _id = id;
    }

    /**
     * Только для статических рефлектов.
     *
     * @param id <= 0
     * @return ref
     */
    public static Reflection createReflection(final int id) {
        if (id > 0) {
            throw new IllegalArgumentException("id should be <= 0");
        }

        return new Reflection(id);
    }

    public int getId() {
        return _id;
    }

    public int getInstancedZoneId() {
        return _instance == null ? 0 : _instance.getId();
    }

    public Party getParty() {
        return _party;
    }

    public void setParty(final Party party) {
        _party = party;
    }

    public void setCommandChannel(final CommandChannel commandChannel) {
        _commandChannel = commandChannel;
    }

    public void setCollapseIfEmptyTime(final int value) {
        _collapseIfEmptyTime = value;
    }

    public String getName() {
        return _name;
    }

    protected void setName(final String name) {
        _name = name;
    }

    public InstantZone getInstancedZone() {
        return _instance;
    }

    protected void setInstancedZone(final InstantZone iz) {
        _instance = iz;
    }

    public int getGeoIndex() {
        return _geoIndex;
    }

    protected void setGeoIndex(final int geoIndex) {
        _geoIndex = geoIndex;
    }

    public Location getCoreLoc() {
        return _resetLoc;
    }

    public void setCoreLoc(final Location l) {
        _resetLoc = l;
    }

    public Location getReturnLoc() {
        return _returnLoc;
    }

    public void setReturnLoc(final Location l) {
        _returnLoc = l;
    }

    public Location getTeleportLoc() {
        return _teleportLoc;
    }

    public void setTeleportLoc(final Location l) {
        _teleportLoc = l;
    }

    public List<Spawner> getSpawns() {
        return _spawns;
    }

    public Collection<DoorInstance> getDoors() {
        return _doors.values();
    }

    public DoorInstance getDoor(final int id) {
        return _doors.get(id);
    }

    public Zone getZone(final String name) {
        return _zones.get(name);
    }

    /**
     * Время в мс
     *
     * @param timeInMillis
     */
    public void startCollapseTimer(final long timeInMillis) {
        if (isDefault()) {
            new Exception("Basic reflection " + _id + " could not be collapsed!").printStackTrace();
            return;
        }
        final SystemMessage message = new SystemMessage(SystemMsg.THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES).addNumber(timeInMillis / 60 / 1000);

        lock.lock();
        try {
            if (_collapseTask != null) {
                _collapseTask.cancel(false);
                _collapseTask = null;
            }
            if (_collapse1minTask != null) {
                _collapse1minTask.cancel(false);
                _collapse1minTask = null;
            }
            _collapseTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                @Override
                public void runImpl() throws Exception {
                    collapse();
                }
            }, timeInMillis);

            if (timeInMillis > 61 * 1000) {
                for (final GameObject o : _objects) {
                    if (o.isPlayer()) {
                        ((Player) o).sendPacket(message);
                    }
                }
            } else {
                _collapse1minTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception {
                        minuteBeforeCollapse();
                    }
                }, 60 * 1000L);
            }
        } finally {
            lock.unlock();
        }
    }

    public void stopCollapseTimer() {
        lock.lock();
        try {
            if (_collapseTask != null) {
                _collapseTask.cancel(false);
                _collapseTask = null;
            }

            if (_collapse1minTask != null) {
                _collapse1minTask.cancel(false);
                _collapse1minTask = null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void minuteBeforeCollapse() {
        if (_isCollapseStarted) {
            return;
        }
        final SystemMessage message = new SystemMessage(
                SystemMsg.THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES).addNumber(1);

        lock.lock();
        try {
            for (final GameObject o : _objects) {
                if (o.isPlayer()) {
                    ((Player) o).sendPacket(message);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void collapse() {
        if (_id <= 0) {
            new Exception("Basic reflection " + _id + " could not be collapsed!").printStackTrace();
            return;
        }

        lock.lock();
        try {
            if (_isCollapseStarted) {
                return;
            }

            _isCollapseStarted = true;
        } finally {
            lock.unlock();
        }
        listeners.onCollapse();
        try {
            stopCollapseTimer();
            if (_hiddencollapseTask != null) {
                _hiddencollapseTask.cancel(false);
                _hiddencollapseTask = null;
            }

            for (final Spawner s : _spawns) {
                s.deleteAll();
            }

            for (final String group : _spawners.keySet()) {
                despawnByGroup(group);
            }

            for (final DoorInstance d : _doors.values()) {
                d.deleteMe();
            }
            _doors.clear();

            for (final Zone zone : _zones.values()) {
                zone.setActive(false);
            }
            _zones.clear();

            final List<Player> teleport = new ArrayList<>();
            final List<GameObject> delete = new ArrayList<>();

            lock.lock();
            try {
                for (final GameObject o : _objects) {
                    if (o.isPlayer()) {
                        teleport.add((Player) o);
                    } else if (!o.isPlayable()) {
                        delete.add(o);
                    }
                }
            } finally {
                lock.unlock();
            }

            for (final Player player : teleport) {
                if (player.getParty() != null) {
                    if (equals(player.getParty().getReflection())) {
                        player.getParty().setReflection(null);
                    }
                    if (player.getParty().getCommandChannel() != null && equals(player.getParty().getCommandChannel().getReflection())) {
                        player.getParty().getCommandChannel().setReflection(null);
                    }
                }
                if (equals(player.getReflection())) {
                    if (getReturnLoc() != null) {
                        player.teleToLocation(getReturnLoc(), ReflectionManager.DEFAULT);
                    } else {
                        player.setReflection(ReflectionManager.DEFAULT);
                    }
                }
            }

            if (_commandChannel != null) {
                _commandChannel.setReflection(null);
                _commandChannel = null;
            }

            if (_party != null) {
                _party.setReflection(null);
                _party = null;
            }

            for (final GameObject o : delete) {
                o.deleteMe();
            }

            _spawns.clear();
            _objects.clear();
            _visitors.clear();
            _doors.clear();

            _playerCount = 0;

            onCollapse();
        } finally {
            ReflectionManager.getInstance().remove(this);
            GeoEngine.FreeGeoIndex(getGeoIndex());
        }
    }

    protected void onCollapse() {
    }

    public void addObject(final GameObject o) {
        if (_isCollapseStarted) {
            return;
        }

        boolean stopCollapseTask = false;

        lock.lock();
        try {
            _objects.add(o);
            if (o.isPlayer()) {
                _playerCount++;
                _visitors.add(o.getObjectId());
                onPlayerEnter(o.getPlayer());
                stopCollapseTask = _playerCount == 1;
            }
        } finally {
            lock.unlock();
        }

        if (stopCollapseTask && _hiddencollapseTask != null) {
            _hiddencollapseTask.cancel(false);
            _hiddencollapseTask = null;
        }
    }

    public void removeObject(final GameObject o) {
        if (_isCollapseStarted) {
            return;
        }

        boolean startCollapseTask = false;

        lock.lock();
        try {
            if (!_objects.remove(o)) {
                return;
            }
            if (o.isPlayer()) {
                _playerCount--;
                onPlayerExit(o.getPlayer());
                startCollapseTask = (_playerCount == 0) && !isDefault();
            }
        } finally {
            lock.unlock();
        }

        if (startCollapseTask && _hiddencollapseTask == null) {
            if (_collapseIfEmptyTime <= 0) {
                collapse();
            } else {
                _hiddencollapseTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception {
                        collapse();
                    }
                }, _collapseIfEmptyTime * 60 * 1000L);
            }
        }
    }

    public void onPlayerEnter(final Player player) {
        // Unequip forbidden for this instance items
        player.getInventory().validateItems();
    }

    public void onPlayerExit(final Player player) {
        // Unequip forbidden for this instance items
        player.getInventory().validateItems();
    }

    public List<Player> getPlayers() {
        final List<Player> result = new ArrayList<>();
        lock.lock();
        try {
            for (final GameObject o : _objects) {
                if (o.isPlayer()) {
                    result.add((Player) o);
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public List<NpcInstance> getNpcs() {
        final List<NpcInstance> result = new ArrayList<>();
        lock.lock();
        try {
            for (final GameObject o : _objects) {
                if (o.isNpc()) {
                    result.add((NpcInstance) o);
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public List<NpcInstance> getAllByNpcId(final int npcId, final boolean onlyAlive) {
        final List<NpcInstance> result = new ArrayList<>();
        lock.lock();
        try {
            for (final GameObject o : _objects) {
                if (o.isNpc()) {
                    final NpcInstance npc = (NpcInstance) o;
                    if (npcId == npc.getNpcId() && (!onlyAlive || !npc.isDead())) {
                        result.add(npc);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public boolean canChampions() {
        return _id <= 0;
    }

    public boolean isAutolootForced() {
        return false;
    }

    public boolean isCollapseStarted() {
        return _isCollapseStarted;
    }

    public void addSpawn(final SimpleSpawner spawn) {
        if (spawn != null) {
            _spawns.add(spawn);
        }
    }

    public void fillSpawns(final List<InstantZone.SpawnInfo> si) {
        if (si == null) {
            return;
        }
        for (final InstantZone.SpawnInfo s : si) {
            SimpleSpawner c;
            switch (s.getSpawnType()) {
                case 0: // точечный спаун, в каждой указанной точке
                    for (final Location loc : s.getCoords()) {
                        c = new SimpleSpawner(s.getNpcId());
                        c.setReflection(this);
                        c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                        c.setAmount(s.getCount());
                        c.setLoc(loc);
                        c.doSpawn(true);
                        if (s.getRespawnDelay() == 0) {
                            c.stopRespawn();
                        } else {
                            c.startRespawn();
                        }
                        addSpawn(c);
                    }
                    break;
                case 1: // один точечный спаун в рандомной точке
                    c = new SimpleSpawner(s.getNpcId());
                    c.setReflection(this);
                    c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                    c.setAmount(1);
                    c.setLoc(s.getCoords().get(Rnd.get(s.getCoords().size())));
                    c.doSpawn(true);
                    if (s.getRespawnDelay() == 0) {
                        c.stopRespawn();
                    } else {
                        c.startRespawn();
                    }
                    addSpawn(c);
                    break;
                case 2: // локационный спаун
                    c = new SimpleSpawner(s.getNpcId());
                    c.setReflection(this);
                    c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                    c.setAmount(s.getCount());
                    c.setTerritory(s.getLoc());
                    for (int j = 0; j < s.getCount(); j++) {
                        c.doSpawn(true);
                    }
                    if (s.getRespawnDelay() == 0) {
                        c.stopRespawn();
                    } else {
                        c.startRespawn();
                    }
                    addSpawn(c);
            }
        }
    }

    //FIXME [VISTALL] сдвинуть в один?
    public void init(final TIntObjectMap<DoorTemplate> doors, final Map<String, ZoneTemplate> zones) {
        if (!doors.isEmpty()) {
            _doors = new HashMap<>(doors.size());
        }

        for (final DoorTemplate template : doors.valueCollection()) {
            final DoorInstance door = new DoorInstance(IdFactory.getInstance().getNextId(), template);
            door.setReflection(this);
            door.setIsInvul(true);
            door.spawnMe(template.getLoc());
            if (template.isOpened()) {
                door.openMe();
            }

            _doors.put(template.getNpcId(), door);
        }

        initDoors();

        if (!zones.isEmpty()) {
            _zones = new HashMap<>(zones.size());
        }

        for (final ZoneTemplate template : zones.values()) {
            final Zone zone = new Zone(template);
            zone.setReflection(this);
            switch (zone.getType()) {
                case no_landing:
                case SIEGE:
                    zone.addListener(NoLandingZoneListener.STATIC);
                    break;
                case AirshipController:
                    zone.addListener(new AirshipControllerZoneListener());
                    break;
                case RESIDENCE:
                    zone.addListener(ResidenceEnterLeaveListenerImpl.STATIC);
                    break;
                case dummy:
                    zone.addListener(new NoMiniMapZoneListener());
                    break;
                case peace_zone:
                    zone.addListener(DuelZoneEnterLeaveListenerImpl.STATIC);
                    zone.addListener(DominionWardEnterLeaveListenerImpl.STATIC);
                    if (CustomConfig.subscriptionAllow) {
                        zone.addListener(new SubscriptionPeaceZoneListener());
                    }
                    break;
                case water:
                    zone.addListener(WaterEnterLeaveListenerImpl.STATIC);
                    break;
            }

            if (template.isEnabled()) {
                zone.setActive(true);
            }

            _zones.put(template.getName(), zone);
        }
    }

    //FIXME [VISTALL] сдвинуть в один?
    protected void init0(final Map<Integer, InstantZone.DoorInfo> doors, final Map<String, InstantZone.ZoneInfo> zones) {
        if (!doors.isEmpty()) {
            _doors = new HashMap<>(doors.size());
        }

        for (final InstantZone.DoorInfo info : doors.values()) {
            final DoorInstance door = new DoorInstance(IdFactory.getInstance().getNextId(), info.getTemplate());
            door.setReflection(this);
            door.setIsInvul(info.isInvul());
            door.spawnMe(info.getTemplate().getLoc());
            if (info.isOpened()) {
                door.openMe();
            }

            _doors.put(info.getTemplate().getNpcId(), door);
        }

        initDoors();

        if (!zones.isEmpty()) {
            _zones = new HashMap<>(zones.size());
        }

        for (final InstantZone.ZoneInfo t : zones.values()) {
            final Zone zone = new Zone(t.getTemplate());
            zone.setReflection(this);
            switch (zone.getType()) {
                case no_landing:
                case SIEGE:
                    zone.addListener(NoLandingZoneListener.STATIC);
                    break;
                case AirshipController:
                    zone.addListener(new AirshipControllerZoneListener());
                    break;
                case RESIDENCE:
                    zone.addListener(ResidenceEnterLeaveListenerImpl.STATIC);
                    break;
                case peace_zone:
                    zone.addListener(DuelZoneEnterLeaveListenerImpl.STATIC);
                    zone.addListener(DominionWardEnterLeaveListenerImpl.STATIC);
                    break;
            }

            if (t.isActive()) {
                zone.setActive(true);
            }

            _zones.put(t.getTemplate().getName(), zone);
        }
    }

    private void initDoors() {
        for (final DoorInstance door : _doors.values()) {
            if (door.getTemplate().getMasterDoor() > 0) {
                final DoorInstance masterDoor = getDoor(door.getTemplate().getMasterDoor());

                masterDoor.addListener(new MasterOnOpenCloseListenerImpl(door));
            }
        }
    }

    /**
     * Открывает дверь в отражении
     */
    public void openDoor(final int doorId) {
        final DoorInstance door = _doors.get(doorId);
        if (door != null) {
            door.openMe();
        }
    }

    /**
     * Закрывает дверь в отражении
     */
    public void closeDoor(final int doorId) {
        final DoorInstance door = _doors.get(doorId);
        if (door != null) {
            door.closeMe();
        }
    }

    /**
     * Удаляет все спауны из рефлекшена и запускает коллапс-таймер. Время указывается в минутах.
     */
    public void clearReflection(final int timeInMinutes, final boolean message) {
        if (isDefault()) {
            return;
        }

        for (final NpcInstance n : getNpcs()) {
            n.deleteMe();
        }

        startCollapseTimer(timeInMinutes * 60 * 1000L);

        if (message) {
            for (final Player pl : getPlayers()) {
                if (pl != null) {
                    pl.sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timeInMinutes));
                }
            }
        }
    }

    public NpcInstance addSpawnWithoutRespawn(final int npcId, final Location loc, final int randomOffset) {
        final Location newLoc;
        if (randomOffset > 0) {
            newLoc = Location.findPointToStay(loc, 0, randomOffset, getGeoIndex()).setH(loc.h);
        } else {
            newLoc = loc;
        }

        return NpcUtils.spawnSingle(npcId, newLoc, this);
    }

    public NpcInstance addSpawnWithRespawn(final int npcId, final Location loc, final int randomOffset, final int respawnDelay) {
        final SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(npcId));
        sp.setLoc(randomOffset > 0 ? Location.findPointToStay(loc, 0, randomOffset, getGeoIndex()) : loc);
        sp.setReflection(this);
        sp.setAmount(1);
        sp.setRespawnDelay(respawnDelay);
        sp.doSpawn(true);
        sp.startRespawn();
        return sp.getLastSpawn();
    }

    public boolean isDefault() {
        return getId() <= 0;
    }

    public int[] getVisitors() {
        return !_visitors.isEmpty() ? _visitors.toArray() : ArrayUtils.EMPTY_INT_ARRAY;
    }

    public boolean isVisitor(final Player player) {
        for (final int charId : getVisitors())
            if (player.getObjectId() == charId)
                return true;
        return false;
    }

    public void setReenterTime(final long time) {
        int[] players = null;
        lock.lock();
        try {
            players = _visitors.toArray();
        } finally {
            lock.unlock();
        }

        if (players != null) {
            Player player;

            for (final int objectId : players) {
                try {
                    player = World.getPlayer(objectId);
                    if (player != null) {
                        player.setInstanceReuse(getInstancedZoneId(), time);
                    } else {
                        CharacterInstancesDAO.getInstance().setInstanceReenterTime(objectId, getInstancedZoneId(), time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onCreate() {
        ReflectionManager.getInstance().add(this);
    }

    public void init(final InstantZone instantZone) {
        setName(instantZone.getName());
        setInstancedZone(instantZone);

        if (instantZone.getMapX() >= 0) {
            final int geoIndex = GeoEngine.NextGeoIndex(instantZone.getMapX(), instantZone.getMapY(), getId());
            setGeoIndex(geoIndex);
        }

        setTeleportLoc(instantZone.getTeleportCoord());
        if (instantZone.getReturnCoords() != null) {
            setReturnLoc(instantZone.getReturnCoords());
        }
        fillSpawns(instantZone.getSpawnsInfo());

        if (!instantZone.getSpawns().isEmpty()) {
            _spawners = new HashMap<>(instantZone.getSpawns().size());
            for (final Map.Entry<String, InstantZone.SpawnInfo2> entry : instantZone.getSpawns().entrySet()) {
                final List<Spawner> spawnList = new ArrayList<>(entry.getValue().getTemplates().size());
                _spawners.put(entry.getKey(), spawnList);

                for (final SpawnTemplate template : entry.getValue().getTemplates()) {
                    final Spawner spawner = new HardSpawner(template);
                    spawnList.add(spawner);

                    spawner.setAmount(template.getCount());
                    spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
                    spawner.setReflection(this);
                    spawner.setRespawnTime(0);
                }

                if (entry.getValue().isSpawned()) {
                    spawnByGroup(entry.getKey());
                }
            }
        }

        init0(instantZone.getDoors(), instantZone.getZones());
        setCollapseIfEmptyTime(instantZone.getCollapseIfEmpty());
        startCollapseTimer(instantZone.getTimelimit() * 60 * 1000L);

        onCreate();
    }

    public void spawnByGroup(final String name) {
        final List<Spawner> list = _spawners.get(name);
        if (list == null) {
            throw new IllegalArgumentException();
        }

        for (final Spawner s : list) {
            s.init();
        }
    }

    public void despawnByGroup(final String name) {
        final List<Spawner> list = _spawners.get(name);
        if (list == null) {
            throw new IllegalArgumentException();
        }

        for (final Spawner s : list) {
            s.deleteAll();
        }
    }

    public Collection<Zone> getZones() {
        return _zones.values();
    }

    public <T extends Listener<Reflection>> boolean addListener(final T listener) {
        return listeners.add(listener);
    }

    public <T extends Listener<Reflection>> boolean removeListener(final T listener) {
        return listeners.remove(listener);
    }

    public void clearVisitors() {
        _visitors.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Reflection))
            return false;

        final Reflection that = (Reflection) o;

        if (_id != that._id)
            return false;
        if (!_name.equals(that._name))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id;
        result = 31 * result + _name.hashCode();
        return result;
    }

    public class ReflectionListenerList extends ListenerList<Reflection> {
        public void onCollapse() {
            for (final OnReflectionCollapseListener listener : getListeners(OnReflectionCollapseListener.class))
                listener.onReflectionCollapse(Reflection.this);
        }
    }
}
