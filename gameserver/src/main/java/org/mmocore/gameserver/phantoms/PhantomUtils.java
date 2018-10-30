package org.mmocore.gameserver.phantoms;

import org.mmocore.commons.utils.Rnd;

/**
 * Created by Hack
 * Date: 23.08.2016 7:45
 */
public class PhantomUtils {
    public static long getRndDelay(long delay) {
        return Rnd.get(delay, delay + delay / 2);
    }
}
