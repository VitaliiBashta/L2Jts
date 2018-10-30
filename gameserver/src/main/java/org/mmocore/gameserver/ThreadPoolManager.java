package org.mmocore.gameserver;

import org.mmocore.commons.threading.LoggingRejectedExecutionHandler;
import org.mmocore.commons.threading.PriorityThreadFactory;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.threading.RunnableStatsWrapper;
import org.mmocore.gameserver.configuration.config.ServerConfig;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private final ScheduledThreadPoolExecutor scheduledExecutor;
    private final ThreadPoolExecutor executor;
    private final ThreadPoolExecutor pathfindThreadPool;
    private volatile boolean shutdown;

    private ThreadPoolManager() {
        scheduledExecutor = new ScheduledThreadPoolExecutor(ServerConfig.SCHEDULED_THREAD_POOL_SIZE, new PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY), new LoggingRejectedExecutionHandler());
        executor = new ThreadPoolExecutor(ServerConfig.EXECUTOR_THREAD_POOL_SIZE, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("ThreadPoolExecutor", Thread.NORM_PRIORITY), new LoggingRejectedExecutionHandler());
        pathfindThreadPool = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("Pathfind Pool", Thread.NORM_PRIORITY), new LoggingRejectedExecutionHandler());

        pathfindThreadPool.allowCoreThreadTimeOut(true);
        //Очистка каждые 5 минут
        scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                scheduledExecutor.purge();
                executor.purge();
                pathfindThreadPool.purge();
            }
        }, 5L, 5L, TimeUnit.MINUTES);
    }

    public static ThreadPoolManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static Runnable wrap(final Runnable r) {
        return ServerConfig.ENABLE_RUNNABLE_STATS ? RunnableStatsWrapper.wrap(r) : r;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
        return schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> schedule(final Runnable r, final long delay, final TimeUnit timeUnit) {
        return scheduledExecutor.schedule(wrap(r), delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initial, final long delay) {
        return scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initial, final long delay, final TimeUnit timeUnit) {
        return scheduledExecutor.scheduleAtFixedRate(wrap(r), initial, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleAtFixedDelay(final Runnable r, final long initial, final long delay) {
        return scheduleAtFixedDelay(r, initial, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedDelay(final Runnable r, final long initial, final long delay, final TimeUnit timeUnit) {
        return scheduledExecutor.scheduleWithFixedDelay(wrap(r), initial, delay, timeUnit);
    }

    public void execute(final Runnable r) {
        executor.execute(wrap(r));
    }

    public void executePathfind(final Runnable r) {
        pathfindThreadPool.execute(wrap(r));
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;
        try {
            scheduledExecutor.shutdown();
            pathfindThreadPool.shutdown();

            scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);
            pathfindThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    public CharSequence getStats() {
        final StringBuilder list = new StringBuilder();

        list.append("ScheduledThreadPool\n");
        list.append("=================================================\n");
        list.append("\tgetActiveCount: ...... ").append(scheduledExecutor.getActiveCount()).append('\n');
        list.append("\tgetCorePoolSize: ..... ").append(scheduledExecutor.getCorePoolSize()).append('\n');
        list.append("\tgetPoolSize: ......... ").append(scheduledExecutor.getPoolSize()).append('\n');
        list.append("\tgetLargestPoolSize: .. ").append(scheduledExecutor.getLargestPoolSize()).append('\n');
        list.append("\tgetMaximumPoolSize: .. ").append(scheduledExecutor.getMaximumPoolSize()).append('\n');
        list.append("\tgetCompletedTaskCount: ").append(scheduledExecutor.getCompletedTaskCount()).append('\n');
        list.append("\tgetQueuedTaskCount: .. ").append(scheduledExecutor.getQueue().size()).append('\n');
        list.append("\tgetTaskCount: ........ ").append(scheduledExecutor.getTaskCount()).append('\n');
        list.append("ThreadPoolExecutor\n");
        list.append("=================================================\n");
        list.append("\tgetActiveCount: ...... ").append(executor.getActiveCount()).append('\n');
        list.append("\tgetCorePoolSize: ..... ").append(executor.getCorePoolSize()).append('\n');
        list.append("\tgetPoolSize: ......... ").append(executor.getPoolSize()).append('\n');
        list.append("\tgetLargestPoolSize: .. ").append(executor.getLargestPoolSize()).append('\n');
        list.append("\tgetMaximumPoolSize: .. ").append(executor.getMaximumPoolSize()).append('\n');
        list.append("\tgetCompletedTaskCount: ").append(executor.getCompletedTaskCount()).append('\n');
        list.append("\tgetQueuedTaskCount: .. ").append(executor.getQueue().size()).append('\n');
        list.append("\tgetTaskCount: ........ ").append(executor.getTaskCount()).append('\n');
        list.append("PathfindThreadPool\n");
        list.append("=================================================\n");
        list.append("\tgetActiveCount: ...... ").append(pathfindThreadPool.getActiveCount()).append("\n");
        list.append("\tgetCorePoolSize: ..... ").append(pathfindThreadPool.getCorePoolSize()).append("\n");
        list.append("\tgetPoolSize: ......... ").append(pathfindThreadPool.getPoolSize()).append("\n");
        list.append("\tgetLargestPoolSize: .. ").append(pathfindThreadPool.getLargestPoolSize()).append("\n");
        list.append("\tgetMaximumPoolSize: .. ").append(pathfindThreadPool.getMaximumPoolSize()).append("\n");
        list.append("\tgetCompletedTaskCount: ").append(pathfindThreadPool.getCompletedTaskCount()).append("\n");
        list.append("\tgetQueuedTaskCount: .. ").append(pathfindThreadPool.getQueue().size()).append("\n");
        list.append("\tgetTaskCount: ........ ").append(pathfindThreadPool.getTaskCount()).append("\n");

        return list;
    }

    private static class LazyHolder {
        private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }
}
