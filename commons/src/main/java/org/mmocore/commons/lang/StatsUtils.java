package org.mmocore.commons.lang;

import java.lang.management.*;

public final class StatsUtils {
    private static final MemoryMXBean memMXbean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadMXbean = ManagementFactory.getThreadMXBean();

    public static long getMemUsed() {
        return memMXbean.getHeapMemoryUsage().getUsed();
    }

    public static String getMemUsedMb() {
        return getMemUsed() / 0x100000 + " Mb";
    }

    public static long getMemMax() {
        return memMXbean.getHeapMemoryUsage().getMax();
    }

    public static String getMemMaxMb() {
        return getMemMax() / 0x100000 + " Mb";
    }

    public static long getMemFree() {
        final MemoryUsage heapMemoryUsage = memMXbean.getHeapMemoryUsage();
        return heapMemoryUsage.getMax() - heapMemoryUsage.getUsed();
    }

    public static String getMemFreeMb() {
        return getMemFree() / 0x100000 + " Mb";
    }

    public static CharSequence getMemUsage() {
        final double maxMem = memMXbean.getHeapMemoryUsage().getMax() / 1024.; // maxMemory is the upper limit the jvm can use
        final double allocatedMem = memMXbean.getHeapMemoryUsage().getCommitted() / 1024.; //totalMemory the size of the current allocation pool
        final double usedMem = memMXbean.getHeapMemoryUsage().getUsed() / 1024.; // freeMemory the unused memory in the allocation pool
        final double nonAllocatedMem = maxMem - allocatedMem; //non allocated memory till jvm limit
        final double cachedMem = allocatedMem - usedMem; // really used memory
        final double useableMem = maxMem - usedMem; //allocated, but non-used and non-allocated memory

        final StringBuilder list = new StringBuilder();

        list.append("AllowedMemory: ........... ").append((int) maxMem).append(" KB").append('\n');
        list.append("     Allocated: .......... ").append((int) allocatedMem).append(" KB (").append(((double) Math.round(allocatedMem / maxMem * 1000000) / 10000)).append("%)").append('\n');
        list.append("     Non-Allocated: ...... ").append((int) nonAllocatedMem).append(" KB (").append((double) Math.round(nonAllocatedMem / maxMem * 1000000) / 10000).append("%)").append('\n');
        list.append("AllocatedMemory: ......... ").append((int) allocatedMem).append(" KB").append('\n');
        list.append("     Used: ............... ").append((int) usedMem).append(" KB (").append((double) Math.round(usedMem / maxMem * 1000000) / 10000).append("%)").append('\n');
        list.append("     Unused (cached): .... ").append((int) cachedMem).append(" KB (").append((double) Math.round(cachedMem / maxMem * 1000000) / 10000).append("%)").append('\n');
        list.append("UseableMemory: ........... ").append((int) useableMem).append(" KB (").append((double) Math.round(useableMem / maxMem * 1000000) / 10000).append("%)").append('\n');

        return list;
    }

    public static CharSequence getThreadStats() {
        final StringBuilder list = new StringBuilder();

        final int threadCount = threadMXbean.getThreadCount();
        final int daemonCount = threadMXbean.getThreadCount();
        final int nonDaemonCount = threadCount - daemonCount;
        final int peakCount = threadMXbean.getPeakThreadCount();
        final long totalCount = threadMXbean.getTotalStartedThreadCount();

        list.append("Live: .................... ").append(threadCount).append(" threads").append('\n');
        list.append("     Non-Daemon: ......... ").append(nonDaemonCount).append(" threads").append('\n');
        list.append("     Daemon: ............. ").append(daemonCount).append(" threads").append('\n');
        list.append("Peak: .................... ").append(peakCount).append(" threads").append('\n');
        list.append("Total started: ........... ").append(totalCount).append(" threads").append('\n');
        list.append("=================================================").append('\n');

        return list;
    }

    public static CharSequence getThreadStats(final boolean lockedMonitors, final boolean lockedSynchronizers, final boolean stackTrace) {
        final StringBuilder list = new StringBuilder();

        for (final ThreadInfo info : threadMXbean.dumpAllThreads(lockedMonitors, lockedSynchronizers)) {
            list.append("Thread #").append(info.getThreadId()).append(" (").append(info.getThreadName()).append(')').append('\n');
            list.append("=================================================\n");
            list.append("\tgetThreadState: ...... ").append(info.getThreadState()).append('\n');
            for (final MonitorInfo monitorInfo : info.getLockedMonitors()) {
                list.append("\tLocked monitor: ....... ").append(monitorInfo).append('\n');
                list.append("\t\t[").append(monitorInfo.getLockedStackDepth()).append(".]: at ").append(monitorInfo.getLockedStackFrame()).append('\n');
            }

            for (final LockInfo lockInfo : info.getLockedSynchronizers()) {
                list.append("\tLocked synchronizer: ...").append(lockInfo).append('\n');
            }

            if (stackTrace) {
                list.append("\tgetStackTace: ..........\n");
                for (final StackTraceElement trace : info.getStackTrace()) {
                    list.append("\t\tat ").append(trace).append('\n');
                }
            }
            list.append("=================================================\n");
        }

        return list;
    }

    public static CharSequence getGCStats() {
        final StringBuilder list = new StringBuilder();

        for (final GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            list.append("GarbageCollector (").append(gcBean.getName()).append(")\n");
            list.append("=================================================\n");
            list.append("getCollectionCount: ..... ").append(gcBean.getCollectionCount()).append('\n');
            list.append("getCollectionTime: ...... ").append(gcBean.getCollectionTime()).append(" ms").append('\n');
            list.append("=================================================\n");
        }

        return list;
    }
}