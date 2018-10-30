package org.mmocore.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 20:49/30.11.2010
 */
public abstract class LoggerObject {
    protected final Logger _log = LoggerFactory.getLogger(getClass());

    public void error(final String st, final Exception e) {
        _log.error(getClass().getSimpleName() + ": " + st, e);
    }

    public void error(final String st) {
        _log.error(getClass().getSimpleName() + ": " + st);
    }

    public void warn(final String st, final Exception e) {
        _log.warn(getClass().getSimpleName() + ": " + st, e);
    }

    public void warn(final String st) {
        _log.warn(getClass().getSimpleName() + ": " + st);
    }

    public void info(final String st, final Exception e) {
        _log.info(getClass().getSimpleName() + ": " + st, e);
    }

    public void info(final String st) {
        _log.info(getClass().getSimpleName() + ": " + st);
    }
}
