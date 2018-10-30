package org.mmocore.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory {
    private static final Logger _log = LoggerFactory.getLogger(PriorityThreadFactory.class);

    private final int _prio;
    private final String _name;
    private final AtomicInteger _threadNumber = new AtomicInteger(1);
    private final ThreadGroup _group;

    public PriorityThreadFactory(final String name, final int prio) {
        _prio = prio;
        _name = name;
        _group = new ThreadGroup(_name);
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(_group, r) {
            @Override
            public void run() {
                try {
                    super.run();
                } catch (Exception e) {
                    _log.info("Exception: " + e, e);
                }
            }
        };
        t.setName(_name + '-' + _threadNumber.getAndIncrement());
        t.setPriority(_prio);
        return t;
    }

    public ThreadGroup getGroup() {
        return _group;
    }
}
