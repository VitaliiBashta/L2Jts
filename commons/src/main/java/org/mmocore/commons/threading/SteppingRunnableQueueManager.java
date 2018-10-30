package org.mmocore.commons.threading;

import org.apache.commons.lang3.mutable.MutableLong;
import org.mmocore.commons.collections.LazyArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Менеджер очереди задач с кратным запланированным временем выполнения.
 *
 * @author G1ta0
 */
public abstract class SteppingRunnableQueueManager implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SteppingRunnableQueueManager.class);

    protected final long tickPerStepInMillis;
    private final Collection<SteppingScheduledFuture<?>> queue = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isRunning = new AtomicBoolean();

    public SteppingRunnableQueueManager(final long tickPerStepInMillis) {
        this.tickPerStepInMillis = tickPerStepInMillis;
    }

    /**
     * Запланировать выполнение задачи через промежуток времени
     *
     * @param r     задача для выполнения
     * @param delay задержка в миллисекундах
     * @return SteppingScheduledFuture управляющий объект, отвечающий за выполенение задачи
     */
    public SteppingScheduledFuture<?> schedule(final Runnable r, final long delay) {
        return schedule(r, delay, delay, false);
    }

    /**
     * Запланировать выполнение задачи через равные промежутки времени, с начальной задержкой
     *
     * @param r       задача для выполнения
     * @param initial начальная задержка в миллисекундах
     * @param delay   период выполенения в силлисекундах
     * @return SteppingScheduledFuture управляющий объект, отвечающий за выполенение задачи
     */
    public SteppingScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initial, final long delay) {
        return schedule(r, initial, delay, true);
    }

    private SteppingScheduledFuture<?> schedule(final Runnable r, final long initial, final long delay, final boolean isPeriodic) {
        final SteppingScheduledFuture<?> sr;

        final long initialStepping = getStepping(initial);
        final long stepping = getStepping(delay);

        queue.add(sr = new SteppingScheduledFuture<Boolean>(r, initialStepping, stepping, isPeriodic));

        return sr;
    }

    /**
     * Выбираем "степпинг" для работы задачи:
     * если delay меньше шага выполнения, результат будет равен 1
     * если delay больше шага выполнения, результат будет результатом округления от деления delay / step
     */
    private long getStepping(long delay) {
        delay = Math.max(0, delay);
        return delay % tickPerStepInMillis > tickPerStepInMillis / 2 ? delay / tickPerStepInMillis + 1 : delay < tickPerStepInMillis ? 1 : delay / tickPerStepInMillis;
    }

    @Override
    public void run() {
        if (!isRunning.compareAndSet(false, true)) {
            LOGGER.warn("Slow running queue, managed by " + this + ", queue size : " + queue.size() + '!');
            return;
        }

        try {
            if (queue.isEmpty()) {
                return;
            }

            for (final SteppingScheduledFuture<?> sr : queue) {
                if (!sr.isDone()) {
                    sr.run();
                }
            }
        } finally {
            isRunning.set(false);
        }
    }

    /**
     * Очистить очередь от выполенных и отмененных задач.
     */
    public void purge() {
        final Collection<SteppingScheduledFuture<?>> purge = new LazyArrayList<>();

        for (final SteppingScheduledFuture<?> sr : queue) {
            if (sr.isDone()) {
                purge.add(sr);
            }
        }

        queue.removeAll(purge);

        purge.clear();
    }

    public CharSequence getStats() {
        final StringBuilder list = new StringBuilder();

        final Map<String, MutableLong> stats = new TreeMap<>();
        int total = 0;
        int done = 0;

        for (final SteppingScheduledFuture<?> sr : queue) {
            if (sr.isDone()) {
                done++;
                continue;
            }
            total++;
            MutableLong count = stats.get(sr.r.getClass().getName());
            if (count == null) {
                stats.put(sr.r.getClass().getName(), count = new MutableLong(1L));
            } else {
                count.increment();
            }
        }

        for (final Entry<String, MutableLong> e : stats.entrySet()) {
            list.append('\t').append(e.getKey()).append(" : ").append(e.getValue().longValue()).append('\n');
        }

        list.append("Scheduled: ....... ").append(total).append('\n');
        list.append("Done/Cancelled: .. ").append(done).append('\n');

        return list;
    }

    public class SteppingScheduledFuture<V> implements RunnableScheduledFuture<V> {
        private final Runnable r;
        private final long stepping;
        private final boolean isPeriodic;

        private long step;
        private boolean isCancelled;

        public SteppingScheduledFuture(final Runnable r, final long initial, final long stepping, final boolean isPeriodic) {
            this.r = r;
            this.step = initial;
            this.stepping = stepping;
            this.isPeriodic = isPeriodic;
        }

        @Override
        public void run() {
            if (--step == 0) {
                try {
                    r.run();
                } catch (Exception e) {
                    LOGGER.info("SteppingScheduledFuture.run():" + e, e);
                    e.printStackTrace(); //FIXME [VISTALL] неизвестно почему недает стак в логер
                } finally {
                    if (isPeriodic) {
                        step = stepping;
                    }
                }
            }
        }

        @Override
        public boolean isDone() {
            return isCancelled || !isPeriodic && step == 0;
        }

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return isCancelled = true;
        }

        @Override
        public V get() {
            return null;
        }

        @Override
        public V get(final long timeout, final TimeUnit unit) {
            return null;
        }

        @Override
        public long getDelay(final TimeUnit unit) {
            return unit.convert(step * tickPerStepInMillis, TimeUnit.MILLISECONDS);
        }

        @Override
        public boolean isPeriodic() {
            return isPeriodic;
        }

        @Override
        public int compareTo(final Delayed o) {
            return 0;
        }

        @Override
        public boolean equals(final Object o) {
            return true;
        }
    }
}
