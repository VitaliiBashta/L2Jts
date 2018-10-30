package org.mmocore.gameserver.manager;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.gameserver.data.xml.holder.DoorHolder;
import org.mmocore.gameserver.data.xml.holder.ZoneHolder;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.utils.Location;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReflectionManager {
    public static final Reflection DEFAULT = Reflection.createReflection(0);
    public static final Reflection PARNASSUS = Reflection.createReflection(-1);
    public static final Reflection GIRAN_HARBOR = Reflection.createReflection(-2);
    public static final Reflection JAIL = Reflection.createReflection(-3);

    public static final Reflection CTF_EVENT = Reflection.createReflection(-4);

    private final TIntObjectHashMap<Reflection> _reflections = new TIntObjectHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private ReflectionManager() {
        add(DEFAULT);
        add(PARNASSUS);
        add(GIRAN_HARBOR);
        add(JAIL);

        // создаем в рефлекте все зоны, и все двери
        DEFAULT.init(DoorHolder.getInstance().getDoors(), ZoneHolder.getInstance().getZones());

        JAIL.setCoreLoc(new Location(-114648, -249384, -2984));
    }

    public static ReflectionManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Reflection get(final int id) {
        readLock.lock();
        try {
            return _reflections.get(id);
        } finally {
            readLock.unlock();
        }
    }

    public Reflection add(final Reflection ref) {
        writeLock.lock();
        try {
            return _reflections.put(ref.getId(), ref);
        } finally {
            writeLock.unlock();
        }
    }

    public Reflection remove(final Reflection ref) {
        writeLock.lock();
        try {
            return _reflections.remove(ref.getId());
        } finally {
            writeLock.unlock();
        }
    }

    public Reflection[] getAll() {
        readLock.lock();
        try {
            return _reflections.values(new Reflection[_reflections.size()]);
        } finally {
            readLock.unlock();
        }
    }

    public int getCountByIzId(final int izId) {
        readLock.lock();
        try {
            int i = 0;
            for (final Reflection r : getAll()) {
                if (r.getInstancedZoneId() == izId) {
                    i++;
                }
            }
            return i;
        } finally {
            readLock.unlock();
        }
    }

    public int size() {
        return _reflections.size();
    }

    private static class LazyHolder {
        private static final ReflectionManager INSTANCE = new ReflectionManager();
    }
}