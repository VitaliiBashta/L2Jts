package org.mmocore.commons.threading;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author G1ta0
 */
public abstract class FIFORunnableQueue<T extends Runnable> implements Runnable {
    private static final int NONE = 0;
    private static final int QUEUED = 1;
    private static final int RUNNING = 2;

    private final AtomicInteger state = new AtomicInteger(NONE);

    private final Queue<T> queue;

    public FIFORunnableQueue(final Queue<T> queue) {
        this.queue = queue;
    }

    public void execute(final T t) {
        queue.add(t);

        if (!state.compareAndSet(NONE, QUEUED))
            return;

        execute();
    }

    protected abstract void execute();

    public void clear() {
        queue.clear();
    }

    @Override
    public void run() {
        if (state.get() == RUNNING)
            return;

        state.set(RUNNING);

        try {
            for (; ; ) {
                final Runnable t = queue.poll();
                if (t == null) {
                    break;
                }

                t.run();
            }
        } finally {
            state.set(NONE);
        }
    }
}
