package org.mmocore.commons.collections;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Данная очередь имеет фиксированную длину, при превышении которой новый обьект записывается, а последний удаляется.
 */
public class FixedQueue<E> extends HashMap<Integer, E> {
    private final int size;
    private Lock lock = new ReentrantLock();
    private HashMap<Integer, E> map;

    public FixedQueue(int size) {
        map = new HashMap<>(size);
        this.size = size;
    }

    public void add(E value) {
        try {
            lock.lock();
            roll();
            map.put(0, value);
        } finally {
            lock.unlock();
        }
    }

    private void roll() {
        for (int i = size - 2; i >= 0; i--)
            if (map.containsKey(i))
                map.put(i + 1, map.get(i));
    }

    public boolean contains(E value) {
        try {
            lock.lock();
            return map.containsValue(value);
        } finally {
            lock.unlock();
        }
    }
}
