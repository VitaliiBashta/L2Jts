package org.mmocore.commons.threading;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NB4L1
 */
public final class RunnableStatsManager {
    private static final RunnableStatsManager _instance = new RunnableStatsManager();
    private final Map<Class<?>, ClassStat> classStats = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    public static RunnableStatsManager getInstance() {
        return _instance;
    }

    public void handleStats(final Class<?> cl, final long runTime) {
        lock.lock();

        try {
            ClassStat stat = classStats.get(cl);

            if (stat == null) {
                stat = new ClassStat(cl);
            }

            stat.runCount++;
            stat.runTime += runTime;
            if (stat.minTime > runTime) {
                stat.minTime = runTime;
            }
            if (stat.maxTime < runTime) {
                stat.maxTime = runTime;
            }
        } finally {
            lock.unlock();
        }
    }

    private List<ClassStat> getSortedClassStats() {
        List<ClassStat> result = Collections.emptyList();

        lock.lock();

        try {
            result = Arrays.asList(classStats.values().toArray(new ClassStat[classStats.size()]));
        } finally {
            lock.unlock();
        }

        Collections.sort(result, (c1, c2) -> {
            if (c1.maxTime < c2.maxTime) {
                return 1;
            }
            if (c1.maxTime == c2.maxTime) {
                return 0;
            }
            return -1;
        });

        return result;
    }

    public CharSequence getStats() {
        final StringBuilder list = new StringBuilder();

        final List<ClassStat> stats = getSortedClassStats();

        for (final ClassStat stat : stats) {
            list.append(stat.clazz.getName()).append(":\n");

            list.append("\tRun: ............ ").append(stat.runCount).append('\n');
            list.append("\tTime: ........... ").append(stat.runTime).append('\n');
            list.append("\tMin: ............ ").append(stat.minTime).append('\n');
            list.append("\tMax: ............ ").append(stat.maxTime).append('\n');
            list.append("\tAverage: ........ ").append(stat.runTime / stat.runCount).append('\n');
        }

        return list;
    }

    private class ClassStat {
        private final Class<?> clazz;
        private long runCount = 0;
        private long runTime = 0;
        private long minTime = Long.MAX_VALUE;
        private long maxTime = Long.MIN_VALUE;

        private ClassStat(final Class<?> cl) {
            clazz = cl;
            classStats.put(cl, this);
        }
    }
}
