package org.mmocore.gameserver.model.dress;

import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 17.08.2016 17:52
 */
public class DressData {
    public static boolean check(Player player) {
        return (player.isForceVisualFlag() && !player.isInOlympiadMode());
    }
}
