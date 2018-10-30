package org.mmocore.gameserver.model.entity.events;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.listener.Listener;
import org.mmocore.commons.listener.ListenerList;
import org.mmocore.commons.logging.LoggerObject;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.listener.event.OnStartStopListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.objects.DoorObject;
import org.mmocore.gameserver.model.entity.events.objects.InitableObject;
import org.mmocore.gameserver.model.entity.events.objects.SpawnableObject;
import org.mmocore.gameserver.model.entity.events.objects.ZoneObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.TimeUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * @author VISTALL
 * @date 12:54/10.12.2010
 */
public abstract class Event extends LoggerObject {
    public static final String EVENT = "event";
    // actions
    protected final Map<Integer, List<EventAction>> onTimeActions = new TreeMap<>();
    protected final List<EventAction> onStartActions = new ArrayList<>(0);
    protected final List<EventAction> onStopActions = new ArrayList<>(0);
    protected final List<EventAction> onInitActions = new ArrayList<>(0);
    // objects
    protected final Map<Object, List<Serializable>> objects = new HashMap<>(0);
    protected final int id;
    protected final String name;
    protected final ListenerListImpl _listenerList = new ListenerListImpl();
    protected Map<Integer, ItemInstance> banishedItems = Collections.emptyMap();
    protected List<Future<?>> _tasks = null;

    protected Event(final MultiValueSet<String> set) {
        this(set.getInteger("id"), set.getString("name"));
    }

    protected Event(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public void initEvent() {
        callActions(onInitActions);

        reCalcNextTime(true);

        printInfo();
    }

    public void startEvent() {
        callActions(onStartActions);

        _listenerList.onStart();
    }

    public void stopEvent() {
        callActions(onStopActions);

        _listenerList.onStop();
    }

    public void printInfo() {
        final Instant startSiegeMillis = startTime();

        if (startSiegeMillis == null) {
            info(getName() + " time - undefined");
        } else {
            info(getName() + " time - " + TimeUtils.dateTimeFormat(startSiegeMillis));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getId() + ';' + getName() + ']';
    }

    protected void callActions(final List<EventAction> actions) {
        for (final EventAction action : actions) {
            action.call(this);
        }
    }
    //===============================================================================================================
    //												Actions
    //===============================================================================================================

    public void addOnStartActions(final List<EventAction> start) {
        onStartActions.addAll(start);
    }

    public void addOnStopActions(final List<EventAction> start) {
        onStopActions.addAll(start);
    }

    public void addOnInitActions(final List<EventAction> start) {
        onInitActions.addAll(start);
    }

    public void addOnTimeAction(final int time, final EventAction action) {
        final List<EventAction> list = onTimeActions.get(time);
        if (list != null) {
            list.add(action);
        } else {
            final List<EventAction> actions = new ArrayList<>(1);
            actions.add(action);
            onTimeActions.put(time, actions);
        }
    }

    public void addOnTimeActions(final int time, final List<EventAction> actions) {
        if (actions.isEmpty()) {
            return;
        }

        final List<EventAction> list = onTimeActions.get(time);
        if (list != null) {
            list.addAll(actions);
        } else {
            onTimeActions.put(time, new ArrayList<>(actions));
        }
    }

    public void timeActions(final int time) {
        final List<EventAction> actions = onTimeActions.get(time);
        if (actions == null) {
            info("Undefined time : " + time);
            return;
        }

        callActions(actions);
    }

    public Integer[] timeActions() {
        return onTimeActions.keySet().toArray(new Integer[onTimeActions.size()]);
    }

    public void registerActions() {
        registerActions(false);
    }

    //===============================================================================================================
    //												Tasks
    //===============================================================================================================

    public synchronized void registerActions(final boolean ignoreStartTime) {
        final Instant startTime;
        if (!ignoreStartTime) {
            startTime = startTime();
            if (startTime == null || startTime.equals(Instant.EPOCH))
                return;
        } else {
            //correct -3 second
            startTime = Instant.now().plusSeconds(180);
        }

        if (_tasks == null)
            _tasks = new ArrayList<>(onTimeActions.size());

        final Instant now = Instant.now();
        for (final int key : onTimeActions.keySet()) {
            final Instant time = startTime.plusSeconds(key);
            final EventTimeTask wrapper = new EventTimeTask(this, key);

            if (!time.isAfter(now))
                ThreadPoolManager.getInstance().execute(wrapper);
            else {
                final long diffInMs = Duration.between(now, time).toMillis();
                _tasks.add(ThreadPoolManager.getInstance().schedule(wrapper, diffInMs));
            }
        }
    }

    public synchronized void clearActions() {
        if (_tasks == null) {
            return;
        }

        for (final Future<?> f : _tasks) {
            f.cancel(false);
        }

        _tasks.clear();
    }

    @SuppressWarnings("unchecked")
    public <O extends Serializable> List<O> getObjects(final Object name) {
        final List<Serializable> objects = this.objects.get(name);
        return objects == null ? Collections.<O>emptyList() : (List<O>) objects;
    }

    //===============================================================================================================
    //												Objects
    //===============================================================================================================

    public <O extends Serializable> O getFirstObject(final Object name) {
        final List<O> objects = getObjects(name);
        return !objects.isEmpty() ? objects.get(0) : null;
    }

    public void addObject(final Object name, final Serializable object) {
        if (object == null) {
            return;
        }

        List<Serializable> list = objects.get(name);
        if (list != null) {
            list.add(object);
        } else {
            list = new CopyOnWriteArrayList<>();
            list.add(object);
            objects.put(name, list);
        }
    }

    public void removeObject(final Object name, final Serializable o) {
        if (o == null) {
            return;
        }

        final List<Serializable> list = objects.get(name);
        if (list != null) {
            list.remove(o);
        }
    }

    @SuppressWarnings("unchecked")
    public <O extends Serializable> List<O> removeObjects(final Object name) {
        final List<Serializable> objects = this.objects.remove(name);
        return objects == null ? Collections.<O>emptyList() : (List<O>) objects;
    }

    public void addObjects(final Object name, final List<? extends Serializable> objects) {
        if (objects.isEmpty()) {
            return;
        }

        final List<Serializable> list = this.objects.get(name);
        if (list != null) {
            list.addAll(objects);
        } else {
            this.objects.put(name, new CopyOnWriteArrayList<>(objects));
        }
    }

    public Map<Object, List<Serializable>> getObjects() {
        return objects;
    }

    public void spawnAction(final Object name, final boolean spawn) {
        final List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            info("Undefined objects: " + name);
            return;
        }

        for (final Serializable object : objects) {
            if (object instanceof SpawnableObject) {
                if (spawn) {
                    ((SpawnableObject) object).spawnObject(this);
                } else {
                    ((SpawnableObject) object).despawnObject(this);
                }
            }
        }
    }

    public void respawnAction(Object name) {
        final List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            info("Undefined objects: " + name);
            return;
        }

        for (Object object : objects) {
            if (object instanceof SpawnableObject) {
                ((SpawnableObject) object).respawnObject(this);
            }
        }
    }

    public void doorAction(final Object name, final boolean open) {
        final List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            info("Undefined objects: " + name);
            return;
        }

        for (final Serializable object : objects) {
            if (object instanceof DoorObject) {
                if (open) {
                    ((DoorObject) object).open(this);
                } else {
                    ((DoorObject) object).close(this);
                }
            }
        }
    }

    public void zoneAction(final Object name, final boolean active) {
        final List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            info("Undefined objects: " + name);
            return;
        }

        for (final Serializable object : objects) {
            if (object instanceof ZoneObject) {
                ((ZoneObject) object).setActive(active, this);
            }
        }
    }

    public void initAction(final Object name) {
        final List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            info("Undefined objects: " + name);
            return;
        }

        for (final Serializable object : objects) {
            if (object instanceof InitableObject) {
                ((InitableObject) object).initObject(this);
            }
        }
    }

    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(EVENT)) {
            if (start) {
                startEvent();
            } else {
                stopEvent();
            }
        }
    }

    public void refreshAction(final Object name) {
        final List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            info("Undefined objects: " + name);
            return;
        }

        for (final Serializable object : objects) {
            if (object instanceof SpawnableObject) {
                ((SpawnableObject) object).refreshObject(this);
            }
        }
    }

    public abstract void reCalcNextTime(boolean onInit);
    //===============================================================================================================
    //												Abstracts
    //===============================================================================================================

    public abstract EventType getType();

    protected abstract Instant startTime();

    //===============================================================================================================
    //												Broadcast
    //===============================================================================================================
    public void broadcastToWorld(final IBroadcastPacket packet) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (player != null) {
                player.sendPacket(packet);
            }
        }
    }

    public void broadcastToWorld(final L2GameServerPacket packet) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (player != null) {
                player.sendPacket(packet);
            }
        }
    }

    //===============================================================================================================
    //												Getters & Setters
    //===============================================================================================================
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GameObject getCenterObject() {
        return null;
    }

    public Reflection getReflection() {
        return ReflectionManager.DEFAULT;
    }

    public int getRelation(final Player thisPlayer, final Player target, final int oldRelation) {
        return oldRelation;
    }

    public int getUserRelation(final Player thisPlayer, final int oldRelation) {
        return oldRelation;
    }

    public void checkRestartLocs(final Player player, final Map<RestartType, Boolean> r) {
        //
    }

    public Location getRestartLoc(final Player player, final RestartType type) {
        return null;
    }

    public boolean canAttack(final Creature target, final Creature attacker, final Skill skill, final boolean force, final boolean nextAttackCheck) {
        return false;
    }

    public SystemMsg checkForAttack(final Creature target, final Creature attacker, final Skill skill, final boolean force) {
        return null;
    }

    public boolean isInProgress() {
        return false;
    }

    public void findEvent(final Player player) {
        //
    }

    public void announce(final int a) {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented announce");
    }

    public void announceFromHolder(final String string) {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented holder for announce");
    }

    public void teleportPlayers(final String teleportWho, final ZoneType zoneType) {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented teleportPlayers");
    }

    public boolean ifVar(final String name) {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented ifVar");
    }

    public List<Player> itemObtainPlayers() {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented itemObtainPlayers");
    }

    public void giveItem(final Player player, final int itemId, final long count) {
        switch (itemId) {
            case ItemTemplate.ITEM_ID_FAME:
                player.setFame(player.getFame() + (int) count, toString());
                break;
            default:
                ItemFunctions.addItem(player, itemId, count);
                break;
        }
    }

    public List<Player> broadcastPlayers(final int range) {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented broadcastPlayers");
    }

    public NpcInstance getNpcByNpcId(int npcId) {
        return GameObjectsStorage.getByNpcId(npcId);
    }

    /**
     * @param active кто ресает
     * @param target кого ресает
     * @param force  ктрл зажат ли
     * @param quiet  если тру, мессаги об ошибке непосылаются
     * @return возращает можно ли реснуть цень
     */
    public boolean canResurrect(final Creature active, final Creature target, final boolean force, final boolean quiet) {
        throw new UnsupportedOperationException(getClass().getName() + " not implemented canResurrect");
    }

    //===============================================================================================================
    //											setEvent helper
    //===============================================================================================================
    public void onAddEvent(final GameObject o) {
        //
    }

    public void onRemoveEvent(final GameObject o) {
        //
    }

    //===============================================================================================================
    //											Banish items
    //===============================================================================================================
    public void addBanishItem(final ItemInstance item) {
        if (banishedItems.isEmpty())
            banishedItems = new ConcurrentHashMap<>();

        banishedItems.put(item.getObjectId(), item);
    }

    public void removeBanishItems() {
        final Iterator<Map.Entry<Integer, ItemInstance>> iterator = banishedItems.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Integer, ItemInstance> entry = iterator.next();
            iterator.remove();

            ItemInstance item = ItemsDAO.getInstance().load(entry.getKey());
            if (item != null) {
                if (item.getOwnerId() > 0) {
                    final GameObject object = GameObjectsStorage.findObject(item.getOwnerId());
                    if (object != null && object.isPlayable()) {
                        ((Playable) object).getInventory().destroyItem(item);
                        object.getPlayer().sendPacket(SystemMessage.removeItems(item));
                    }
                }
                item.delete();
            } else {
                item = entry.getValue();
            }

            item.deleteMe();
        }
    }

    //===============================================================================================================
    //											 Listeners
    //===============================================================================================================
    public void addListener(final Listener<Event> l) {
        _listenerList.add(l);
    }

    public void removeListener(final Listener<Event> l) {
        _listenerList.remove(l);
    }

    //===============================================================================================================
    //											Object
    //===============================================================================================================
    public void cloneTo(final Event e) {
        e.onInitActions.addAll(onInitActions);

        e.onStartActions.addAll(onStartActions);

        e.onStopActions.addAll(onStopActions);

        for (final Map.Entry<Integer, List<EventAction>> entry : onTimeActions.entrySet()) {
            e.addOnTimeActions(entry.getKey(), entry.getValue());
        }
    }

    public boolean unregisterPlayer(Player player) {
        return false;
    }

    private class ListenerListImpl extends ListenerList<Event> {
        public void onStart() {
            for (final OnStartStopListener listener : getListeners(OnStartStopListener.class))
                listener.onStart(Event.this);
        }

        public void onStop() {
            for (final OnStartStopListener listener : getListeners(OnStartStopListener.class))
                listener.onStop(Event.this);
        }
    }
}
