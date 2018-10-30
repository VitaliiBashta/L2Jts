package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author Java-man
 */
@FunctionalInterface
public interface OnForcedDisconnectListener extends PlayerListener {
    void onForcedDisconnect(Player player);
}
