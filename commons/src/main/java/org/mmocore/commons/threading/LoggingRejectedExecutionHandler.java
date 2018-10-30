package org.mmocore.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author NB4L1
 */
public final class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final Logger _log = LoggerFactory.getLogger(LoggingRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }

        _log.error(r + " from " + executor, new RejectedExecutionException());
    }
}
