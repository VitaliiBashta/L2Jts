package org.mmocore.commons.net.nio.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MMOExecutableQueue<T extends MMOClient> implements Queue<ReceivablePacket<T>>, Runnable {
    private static final int NONE = 0;
    private static final int QUEUED = 1;
    private static final int RUNNING = 2;

    private final IMMOExecutor<T> executor;
    private final Queue<ReceivablePacket<T>> queue;

    private final AtomicInteger state = new AtomicInteger(NONE);

    public MMOExecutableQueue(final IMMOExecutor<T> executor) {
        this.executor = executor;
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        while (state.compareAndSet(QUEUED, RUNNING)) {
            try {
                for (; ; ) {
                    final Runnable t = poll();
                    if (t == null) {
                        break;
                    }

                    t.run();
                }
            } finally {
                state.compareAndSet(RUNNING, NONE);
            }
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<ReceivablePacket<T>> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> E[] toArray(final E[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends ReceivablePacket<T>> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean add(final ReceivablePacket<T> e) {
        if (!queue.add(e))
            return false;

        if (state.getAndSet(QUEUED) == NONE)
            executor.execute(this);

        return true;
    }

    @Override
    public boolean offer(final ReceivablePacket<T> e) {
        return queue.offer(e);
    }

    @Override
    public ReceivablePacket<T> remove() {
        return queue.remove();
    }

    @Override
    public ReceivablePacket<T> poll() {
        return queue.poll();
    }

    @Override
    public ReceivablePacket<T> element() {
        return queue.element();
    }

    @Override
    public ReceivablePacket<T> peek() {
        return queue.peek();
    }
}
