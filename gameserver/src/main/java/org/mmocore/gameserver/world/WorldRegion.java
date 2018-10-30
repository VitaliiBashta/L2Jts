package org.mmocore.gameserver.world;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Переработанный класс региона мира
 *
 * @author G1ta0
 */
public final class WorldRegion implements Iterable<GameObject> {
    public static final WorldRegion[] EMPTY_WORLD_REGION_ARRAY = new WorldRegion[0];
    /**
     * Координаты региона в мире
     */
    private final int tileX, tileY, tileZ;
    /**
     * Активен ли регион
     */
    private final AtomicBoolean _isActive = new AtomicBoolean();
    /**
     * Блокировка для чтения/записи объектов из региона
     */
    private final Lock lock = new ReentrantLock();
    /**
     * Все объекты в регионе
     */
    private volatile GameObject[] _objects = GameObject.EMPTY_GAME_OBJECT_ARRAY;
    /**
     * Количество объектов в регионе
     */
    private int _objectsCount = 0;
    /**
     * Зоны пересекающие этот регион
     */
    private volatile Zone[] _zones = Zone.EMPTY_ZONE_ARRAY;
    /**
     * Количество игроков в регионе
     */
    private int _playersCount = 0;
    /**
     * Запланированная задача активации/деактивации текущего и соседних регионов
     */
    private Future<?> _activateTask;
    WorldRegion(final int x, final int y, final int z) {
        tileX = x;
        tileY = y;
        tileZ = z;
    }

    int getX() {
        return tileX;
    }

    int getY() {
        return tileY;
    }

    int getZ() {
        return tileZ;
    }

    void addToPlayers(final GameObject object, final Creature dropper) {
        if (object == null) {
            return;
        }

        Player player = null;
        if (object.isPlayer()) {
            player = (Player) object;
        }

        final int oid = object.getObjectId();
        final int rid = object.getReflectionId();

        Player p;

        for (final GameObject obj : this) {
            if (obj.getObjectId() == oid || obj.getReflectionId() != rid) {
                continue;
            }
            // Если object - игрок, показать ему все видимые обьекты в регионе
            if (player != null) {
                player.sendPacket(player.addVisibleObject(obj, null));
            }

            // Показать обьект всем игрокам и наблюдателям в регионе
            if (obj.isPlayer() || obj.isObservePoint()) {
                if (obj.isPlayer() && ((Player) obj).isInObserverMode()) {
                    continue;
                }

                p = obj.getPlayer();
                p.sendPacket(p.addVisibleObject(object, dropper));
            }
        }
    }

    void removeFromPlayers(final GameObject object) {
        if (object == null) {
            return;
        }

        Player player = null;
        if (object.isPlayer()) {
            player = (Player) object;
        }

        final int oid = object.getObjectId();
        final Reflection rid = object.getReflection();

        Player p;
        List<L2GameServerPacket> d = null;

        for (final GameObject obj : this) {
            if (obj.getObjectId() == oid || obj.getReflection() != rid) {
                continue;
            }

            // Если object - игрок, убрать у него все видимые обьекты в регионе
            if (player != null) {
                player.sendPacket(player.removeVisibleObject(obj, null));
            }

            // Убрать обьект у всех игроков и наблюдателей в регионе
            if (obj.isPlayer() || obj.isObservePoint()) {
                if (obj.isPlayer() && ((Player) obj).isInObserverMode()) {
                    continue;
                }

                p = obj.getPlayer();
                p.sendPacket(p.removeVisibleObject(object, d == null ? d = object.deletePacketList() : d));
            }
        }
    }

    public void addObject(final GameObject obj) {
        if (obj == null) {
            return;
        }

        lock.lock();
        try {
            GameObject[] objects = _objects;

            final GameObject[] resizedObjects = Arrays.copyOf(objects, _objectsCount + 1);
            objects = resizedObjects;
            objects[_objectsCount++] = obj;

            _objects = resizedObjects;

            if (obj.isPlayer()) {
                if (_playersCount++ == 0) {
                    if (_activateTask != null) {
                        _activateTask.cancel(false);
                    }
                    //активируем регион и соседние регионы через секунду
                    _activateTask = ThreadPoolManager.getInstance().schedule(new ActivateTask(true), 1000L);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeObject(final GameObject obj) {
        if (obj == null) {
            return;
        }

        lock.lock();
        try {
            final GameObject[] objects = _objects;

            int index = -1;

            for (int i = 0; i < _objectsCount; i++) {
                if (objects[i].equals(obj)) {
                    index = i;
                    break;
                }
            }

            if (index == -1) //Ошибочная ситуация
            {
                return;
            }

            _objectsCount--;

            final GameObject[] resizedObjects = new GameObject[_objectsCount];
            objects[index] = objects[_objectsCount];
            System.arraycopy(objects, 0, resizedObjects, 0, _objectsCount);

            _objects = resizedObjects;

            if (obj.isPlayer()) {
                if (--_playersCount == 0) {
                    if (_activateTask != null) {
                        _activateTask.cancel(false);
                    }
                    //деактивируем регион и соседние регионы через минуту
                    _activateTask = ThreadPoolManager.getInstance().schedule(new ActivateTask(false), 60000L);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public int getObjectsSize() {
        return _objectsCount;
    }

    public int getPlayersCount() {
        return _playersCount;
    }

    public boolean isEmpty() {
        return _playersCount == 0;
    }

    public boolean isActive() {
        return _isActive.get();
    }

    /**
     * Активация региона, включить или выключить AI всех NPC в регионе
     *
     * @param activate - переключатель
     */
    void setActive(final boolean activate) {
        if (!_isActive.compareAndSet(!activate, activate)) {
            return;
        }

        for (final GameObject obj : this) {
            if (!obj.isNpc()) {
                continue;
            }
            NpcInstance npc = (NpcInstance) obj;
            if (npc.getAI().isActive() != isActive()) {
                if (isActive()) {
                    npc.getAI().startAITask();
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    npc.startRandomAnimation();
                } else if (!npc.getAI().isGlobalAI()) {
                    npc.getAI().stopAITask();
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    npc.stopRandomAnimation();
                }
            }
        }
    }

    void addZone(final Zone zone) {
        lock.lock();
        try {
            _zones = ArrayUtils.add(_zones, zone);
        } finally {
            lock.unlock();
        }
    }

    void removeZone(final Zone zone) {
        lock.lock();
        try {
            _zones = ArrayUtils.remove(_zones, zone);
        } finally {
            lock.unlock();
        }
    }

    public Zone[] getZones() {
        // Без синхронизации и копирования, т.к. удаление/добавление зон происходит достаточно редко
        return _zones;
    }

    @Override
    public String toString() {
        return "[" + tileX + ", " + tileY + ", " + tileZ + ']';
    }

    @Override
    public Iterator<GameObject> iterator() {
        return new InternalIterator(_objects);
    }

    /**
     * Активация/деактивация соседних регионов
     */
    public class ActivateTask extends RunnableImpl {
        private final boolean _isActivating;

        public ActivateTask(final boolean isActivating) {
            _isActivating = isActivating;
        }

        @Override
        public void runImpl()  {
            if (_isActivating) {
                World.activate(WorldRegion.this);
            } else {
                World.deactivate(WorldRegion.this);
            }
        }
    }

    private class InternalIterator implements Iterator<GameObject> {
        final GameObject[] objects;
        int cursor = 0;

        InternalIterator(final GameObject[] objects) {
            this.objects = objects;
        }

        @Override
        public boolean hasNext() {
            if (cursor < objects.length) {
                return objects[cursor] != null;
            }
            return false;
        }

        @Override
        public GameObject next() {
            return objects[cursor++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}