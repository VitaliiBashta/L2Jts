package org.mmocore.gameserver.phantoms.action;

import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.utils.Location;

/**
 * Created by Hack
 * Date: 23.08.2016 18:39
 */
public class RandomMoveAction extends AbstractPhantomAction {
    @Override
    public long getDelay() {
        return 0;
    }

    @Override
    public void run() {
        Location loc = Location.findPointToStay(actor.getSpawnLoc(), PhantomConfig.randomMoveDistance,
                ReflectionManager.DEFAULT.getGeoIndex());
        actor.moveToLocation(loc, 50, true);
    }
}
