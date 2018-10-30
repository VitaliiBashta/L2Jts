package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

@FunctionalInterface
public interface OnPlayerExitListener extends PlayerListener {
    void onPlayerExit(Player player);
}
