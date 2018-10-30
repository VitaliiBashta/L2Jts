package org.mmocore.authserver.manager;

import org.mmocore.commons.threading.RunnableImpl;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private ThreadPoolManager() {
        scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                executor.purge();
                scheduledExecutor.purge();
            }
        }, 10L, 10L, TimeUnit.MINUTES);
    }

    public static ThreadPoolManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void execute(final Runnable r) {
        executor.execute(r);
    }

    public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
        return schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> schedule(final Runnable r, final long delay, final TimeUnit timeUnit) {
        return scheduledExecutor.schedule(r, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initial, final long delay) {
        return scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initial, final long delay, final TimeUnit timeUnit) {
        return scheduledExecutor.scheduleAtFixedRate(r, initial, delay, timeUnit);
    }

    private static class LazyHolder {
        private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }
}
