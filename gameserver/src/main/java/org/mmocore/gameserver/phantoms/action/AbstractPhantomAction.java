package org.mmocore.gameserver.phantoms.action;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.phantoms.PhantomUtils;
import org.mmocore.gameserver.phantoms.model.Phantom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Created by Hack
 * Date: 23.08.2016 7:38
 */
public abstract class AbstractPhantomAction implements Runnable {
    protected static final Logger log = LoggerFactory.getLogger(AbstractPhantomAction.class);
    protected Phantom actor;

    public abstract long getDelay();

    public Future<?> schedule() {
        return ThreadPoolManager.getInstance().schedule(this, PhantomUtils.getRndDelay(getDelay()));
    }

    public void setActor(Phantom phantom) {
        actor = phantom;
    }
}
